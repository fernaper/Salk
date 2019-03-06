from flask import Flask, request, jsonify

import sys
import os

import matplotlib
import numpy as np
import matplotlib.pyplot as plt
import copy
import cv2

# Disable tensorflow compilation warnings

os.environ['TF_CPP_MIN_LOG_LEVEL']='2'
import tensorflow as tf

def predict(image_data):
    predictions = sess.run(softmax_tensor, {'DecodeJpeg/contents:0': image_data})

    # Sort to show labels of first prediction in order of confidence
    top_k = predictions[0].argsort()[-len(predictions[0]):][::-1]

    max_score = 0.0
    res = ''
    for node_id in top_k:
        human_string = label_lines[node_id]
        score = predictions[0][node_id]
        if score > max_score:
            max_score = score
            res = human_string
    return res, max_score

# Loads label file, strips off carriage return
label_lines = [line.rstrip() for line in tf.gfile.GFile("logs/trained_labels.txt")]

# Unpersists graph from file
with tf.gfile.FastGFile("logs/trained_graph.pb", 'rb') as f:
    graph_def = tf.GraphDef()
    graph_def.ParseFromString(f.read())
    _ = tf.import_graph_def(graph_def, name='')


app = Flask(__name__)

@app.route('/', methods=['GET'])
def hello():
    return jsonify({'ip':'88.0.109.140','msg':'Welcome to Salk API','status':1})


@app.route('/get_words', methods=['GET'])
def get_words():
    return jsonify(['hola', 'mundo'])


@app.route('/check_frame', methods=['POST'])
def check_frame():
    return jsonify(['work in progress'])


if __name__ == '__main__':
    app.run(debug=False, host='0.0.0.0', port=5500) #run app on port 5000
