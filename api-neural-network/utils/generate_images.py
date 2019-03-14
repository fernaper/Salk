import sys
import cv2

def main(input_path, output_path, char, skip_frames):
    cap = cv2.VideoCapture(input_path)
    count = 0
    saved = 0
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        if count % skip_frames == 0:
            cv2.imwrite('{}{}_{}.png'.format(output_path,char,saved), frame)
            saved += 1
        count += 1
    cap.release()

if __name__ == '__main__':
    if len(sys.argv) > 4:
        main(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
    else:
        print('Not enught arguments')
