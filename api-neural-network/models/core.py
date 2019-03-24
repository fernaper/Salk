# This method needs to be executed on a linux terminal
import numpy as np
import config
import queue
import cv2
import sys
import os

from multiprocessing.managers import BaseManager

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2' # Disable tensorflow compilation warnings
import tensorflow as tf

sess = None
softmax_tensor = None
# Loads label file, strips off carriage return
label_lines = [line.rstrip() for line in tf.gfile.GFile("logs/trained_labels.txt")]

def manage_connection():
    img_queue = queue.Queue()
    detection_queue = queue.Queue()
    BaseManager.register('img_queue', callable=lambda: img_queue)
    BaseManager.register('detection_queue', callable=lambda: detection_queue)
    process_manager = BaseManager(address=('', 5750), authkey=config.QUEUE_PASS)
    process_manager.start()
    return process_manager


def process_frame(img_queue, detection_queue):
    frame, letter = img_queue.get()
    print('Processing frame')
    #frame = cv2.flip(frame, 1)
    frame = cv2.flip(frame, 0)
    image_data = cv2.imencode('.jpg', frame)[1].tostring()
    if not image_data:
        return False
    answer, confidence = predict(image_data, letter.lower())
    print(' - Answer: {}; Confidence: {}'.format(answer, confidence))
    detection_queue.put((answer, float(confidence)))
    return True


def predict(image_data, letter):
    predictions = sess.run(softmax_tensor, \
             {'DecodeJpeg/contents:0': image_data})
    # Sort to show labels of first prediction in order of confidence
    top_k = predictions[0].argsort()[-len(predictions[0]):][::-1]

    max_score = 0.0
    res = ''
    letter_extra_score = 0.1
    for node_id in top_k:
        human_string = label_lines[node_id]
        score = predictions[0][node_id]
        # We are assuming that the person is more or less getting
        # close to the letter, so we increase a little bit the confidence
        if human_string == letter and score < 1-letter_extra_score:
            score += letter_extra_score
        if score > max_score:
            max_score = score
            res = human_string

    return res, max_score


def main():
    global sess, softmax_tensor

    # Unpersists graph from file
    with tf.gfile.FastGFile("logs/trained_graph.pb", 'rb') as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
        _ = tf.import_graph_def(graph_def, name='')

    with tf.Session() as sess:
        # With this queues we can connect with our API
        process_manager = manage_connection()
        img_queue = process_manager.img_queue()
        detection_queue = process_manager.detection_queue()
        # Feed the image_data as input to the graph and get first prediction
        softmax_tensor = sess.graph.get_tensor_by_name('final_result:0')

        print('Neural network ready!')
        while process_frame(img_queue, detection_queue):
            pass
        print('Connection Closed')

if __name__ == '__main__':
    main()
