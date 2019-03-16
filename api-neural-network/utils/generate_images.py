import sys
import cv2
import os

def main(input_path, output_path, char, skip_frames, right_handed=True):
    if output_path[-1] != '/':
        output_path += '/'
    cap = cv2.VideoCapture(input_path)
    skip_frames = int(skip_frames)
    count = 0
    saved = 0
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        if count % skip_frames == 0:
            img_name = '{path}{char}/{char}_{num}.png'.format(path=output_path,char=char,num=saved)
            if not os.path.isdir('{}{}'.format(output_path,char)):
                os.mkdir('{}{}'.format(output_path,char))
            while os.path.exists(img_name):
                saved += 1
                img_name = '{path}{char}/{char}_{num}.png'.format(path=output_path,char=char,num=saved)
            if not right_handed:
                frame = cv2.flip(frame, 0)
            cv2.imwrite(img_name, frame)
            print(img_name)
            saved += 1
        count += 1
    cap.release()

if __name__ == '__main__':
    num_params = len(sys.argv)
    if num_params > 3:
        right_handed = True
        skip_frames = 10
        if num_params > 4:
            right_handed =  'True' == sys.argv[4]
        if num_params > 5:
            skip_frames = sys.argv[5]
        main(sys.argv[1], sys.argv[2], sys.argv[3], skip_frames, right_handed)
    else:
        print('Not enough arguments required: input_path, output_path, char')
        print('Optional arguments: right_handed, skip_frames')
