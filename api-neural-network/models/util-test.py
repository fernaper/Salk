import queue
import time

from multiprocessing.managers import BaseManager

def connect_neural_network():
    BaseManager.register('img_queue')
    BaseManager.register('detection_queue')
    process_manager = BaseManager(address=('localhost', 5750), authkey=b'basic')
    connected = False
    while not connected:
        try:
            process_manager.connect()
            connected = True
        except ConnectionRefusedError as e:
            print(e)
            time.sleep(1)
    return process_manager, process_manager.img_queue(), process_manager.detection_queue()

def send_neural_network(img_queue, detection_queue, image):
    img_queue.put_nowait(image)
    if not image:
        return False
    return detection_queue.get()

if __name__ == '__main__':
    process_manager, img_queue, detection_queue = connect_neural_network()
    while True:
        msg = input('Put something you want to send: ')
        ans = send_neural_network(img_queue, detection_queue, msg)
        if not msg:
            print('Close connection')
            break
        print('Answer: {}'.format(ans))
