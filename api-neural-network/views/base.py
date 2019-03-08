from multiprocessing.managers import BaseManager
from flask import Flask, request, jsonify

import numpy as np
import config
import queue
import time
import cv2

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
        try:
            process_manager.connect()
            connected = True
        except ConnectionRefusedError as e:
            print(e)
            return None
            count += 1
            time.sleep(1)
    print('Connected to neural network')
    return process_manager

def send_neural_network(img_queue, detection_queue, image):
    img_queue.put_nowait(image)
    if not image.any():
        return False
    return detection_queue.get()

app = Flask(__name__)
process_manager = connect_neural_network()

@app.route('/', methods=['GET'])
def hello():
    return jsonify({'ip':'88.0.109.140','msg':'Welcome to Salk API','status':1})

@app.route('/check_frame', methods=['POST'])
def check_image():
    global process_manager
    if not connected:
        process_manager = connect_neural_network()
        if not connected:
            return jsonify({'error':'It has not been possible to establish a connection to the neural network'})
    numpy_image = np.fromfile(request.files['frame'], np.uint8)
    img = cv2.imdecode(numpy_image, cv2.IMREAD_COLOR)
    prediction, confidence = send_neural_network(process_manager.img_queue(), process_manager.detection_queue(), img)
    return jsonify({'prediction':prediction, 'confidence':confidence})

if __name__ == '__main__':
    app.run(debug=False, host='0.0.0.0', port=5500) #run app on port 5000
