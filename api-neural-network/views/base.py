from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/', methods=['GET'])
def hello():
    return jsonify({'ip':'88.0.109.140','msg':'Welcome to Salk API','status':1})

@app.route('/get_words', methods=['GET'])
def get_words():
    return jsonify(['hola', 'mundo'])

if __name__ == '__main__':
    app.run(debug=False, host='0.0.0.0', port=5500) #run app on port 5000
