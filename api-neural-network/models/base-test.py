# This method needs to be executed on a linux terminal
import multiprocessing
import random
import queue
import time
import sys

from multiprocessing.managers import BaseManager

def manage_connection():
    img_queue = queue.Queue()
    detection_queue = queue.Queue()
    BaseManager.register('img_queue', callable=lambda: img_queue)
    BaseManager.register('detection_queue', callable=lambda: detection_queue)
    process_manager = BaseManager(address=('', 5750), authkey=b'basic')
    process_manager.start()
    return process_manager, process_manager.img_queue(), process_manager.detection_queue()

def process_frame(img_queue, detection_queue):
    frame = img_queue.get()
    if not frame.any():
        return False
    #detect(frame)
    time.sleep(1) # Just to simulate processing
    detection_queue.put_nowait(('A', min(0.9877909335174927, random.uniform(0, 1)+0.3)))
    return True

if __name__ == '__main__':
    process_manager, img_queue, detection_queue = manage_connection()
    while process_frame(img_queue, detection_queue):
        pass
    time.sleep(1)
    print('Closed connection')
