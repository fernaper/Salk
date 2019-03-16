from flask import Flask, jsonify
import model
app = Flask(__name__)

@app.route('/')
def hello():
    return jsonify({"ip": "92.176.178.247"})

@app.route('/connection', methods=['GET'])
def connection():
    answer = model.connection()
    return jsonify({"answer":answer.text})

@app.route('/get_word/<language>')
def get_word(language):
    word = model.get_word(language)
    return jsonify(word)

@app.route('/get_word/<language>/<difficulty>')
def get_word_with_difficulty(language, difficulty):
    word = model.get_word_with_difficulty(language, difficulty)
    return jsonify(word)

@app.route('/get_phrase/<language>/<difficulty>')
def get_phrase_with_difficulty(language, difficulty):
    phrase = model.get_phrase_with_difficulty(language, difficulty)
    return jsonify(phrase)

if __name__ == "__main__":
    app.run(debug = False, host = "0.0.0.0", port = 5754)
