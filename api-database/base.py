from flask import Flask, jsonify, request
import model
import logging

app = Flask(__name__)

@app.route('/')
def hello():
    return jsonify({"ip": "92.176.178.247"})


@app.route('/connection', methods=['GET'])
def connection():
    answer = model.connection()
    return jsonify({"answer":answer})


@app.route('/set_language', methods=['PUT'])
def set_language():
    model.set_language(request.form['user'], request.form['language'])
    return jsonify({"OK":True})


@app.route('/set_difficulty', methods=['PUT'])
def set_difficulty():
    model.set_difficulty(request.form['user'], request.form['difficulty'])
    return jsonify({"OK":True})



@app.route('/get_word', methods=['POST'])
def get_word():
    user_name = request.form['user']
    difficulty = request.form['difficulty']
    language = model.get_language(user_name)
    #app.logger.error("{}|{}|{}".format(user_name, difficulty, language), file=sys.stderr)
    word = model.get_word_with_difficulty(language, difficulty)
    return jsonify({"word":word})


@app.route('/get_phrase', methods=['POST'])
def get_phrase_with_difficulty():
    phrase = model.get_phrase_with_difficulty(language, difficulty)
    return jsonify(phrase)

if __name__ == "__main__":
    app.run(debug = True, host = "0.0.0.0", port = 5754)
