import cv2
import sys
import os

SCALE = 0.15

def process_folder(folder, output_folder):
    for element in os.listdir(folder):
        path = os.path.join(folder, element)
        output_path = os.path.join(output_folder,element)
        if os.path.isdir(path):
            if not os.path.exists(output_path):
                os.makedirs(output_path)
            process_folder(path, output_path)
        elif os.path.isfile(path) and path.endswith(tuple(['.png','.jpg','.jpeg'])) and not os.path.isfile(output_path):
            frame = cv2.imread(path)
            small = cv2.resize(frame, (0,0), fx=SCALE, fy=SCALE)
            (x,y,w,h) = cv2.selectROI('Crop',small, True, False)
            cv2.imwrite(output_path,frame[int(y/SCALE):int((y+h)/SCALE), int(x/SCALE):int((x+w)/SCALE)])

if __name__ == '__main__':
    if len(sys.argv) > 2:
        process_folder(sys.argv[1], sys.argv[2])
    else:
        print('Needed path to input folder and output folder')
