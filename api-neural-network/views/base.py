from multiprocessing.managers import BaseManager
from flask import Flask, request, jsonify

import numpy as np
import threading
import config
import arrow
import queue
import time
import cv2
import base64

# This variable is for not blocking the api until the neural network is connected
# It will try to connect 3 times
# If it can not connect and you ask for a method that needs it, it will retry another 3 times
# If it fails another time, just answer Errror
connected = False

def connect_neural_network():
    global connected

    BaseManager.register('img_queue')
    BaseManager.register('detection_queue')
    process_manager = BaseManager(address=('localhost', 5750), authkey=config.QUEUE_PASS)
    count = 0
    while not connected:
        print('Trying to connect to neural network...')
        try:
            process_manager.connect()
            connected = True
        except ConnectionRefusedError as e:
            print(e)
            count += 1
            if count > 2:
                return None
            time.sleep(1)
    print('Connected to neural network')
    return process_manager

def send_neural_network(img_queue, detection_queue, image, letter):
    img_queue.put((image,letter))
    if not image.any():
        return False
    return detection_queue.get()

app = Flask(__name__)
process_manager = connect_neural_network()
lock = threading.Lock()
historic = []

@app.route('/', methods=['GET'])
def hello():
    return jsonify({'ip':'88.0.109.140','msg':'Welcome to Salk API','status':True, 'neural-network':connected})

@app.route('/check_frame', methods=['POST'])
def check_image():
    global process_manager, historic
    with lock:
        if not connected:
            process_manager = connect_neural_network()
            if not connected:
                return jsonify({'error':'It has not been possible to establish a connection to the neural network'})
        #numpy_image = np.fromfile(request.files['frame'], np.uint8)
        #img = cv2.imdecode(numpy_image, cv2.IMREAD_COLOR)
        print('Processing request')
        b64_img = request.form['frame']
        letter = request.form['letter']
        with open('logs/last_image.png','wb') as image_file:
            image_file.write(base64.b64decode(b64_img))
        img = cv2.imread('logs/last_image.png',0)
        prediction, confidence = send_neural_network(process_manager.img_queue(), process_manager.detection_queue(), img, letter)
        historic.append({
            'expected':letter,
            'prediction':prediction,
            'confidence':confidence,
            'timestamp':arrow.now().format('YYYY-MM-DD HH:mm:ss')
        })
        msg = ' - Expected: {}; Prediction: {}; Confidence: {}'.format(letter, prediction, confidence)
        print(msg)
        return jsonify({'prediction':prediction, 'confidence':confidence})

@app.route('/historic', methods=['GET'])
def check_historic():
    return jsonify(historic[::-1])

if __name__ == '__main__':
    app.run(debug=False, host='0.0.0.0', port=5500) #run app on port 5000
