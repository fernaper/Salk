from flask import Flask, jsonify, request
import model
import logging

app = Flask(__name__)

existing_languages = ['spanish', 'english', 'french', 'german']

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


@app.route('/create_user', methods=['PUT'])
def create_user():
    user_name = request.form['user']
    language = request.form.get('language')
    if language not in existing_languages:
        model.create_user(user_name)
        warning = 'Language requested is not available or misspelled'
    else:
        model.create_user(user_name, language)
        warning = ''

    return jsonify({"OK":True, "warning": warning})


@app.route('/set_difficulty', methods=['PUT'])
def set_difficulty():
    model.set_difficulty(request.form['user'], request.form['difficulty'])
    return jsonify({"OK":True})



@app.route('/get_word', methods=['POST'])
def get_word():
    user_name = request.form['user']
    difficulty = request.form['difficulty']
    language = model.get_language(user_name)
    word = model.get_word_with_difficulty(language, difficulty)
    return jsonify({"word":word})


@app.route('/get_phrase', methods=['POST'])
def get_phrase_with_difficulty():
    user_name = request.form['user']
    difficulty = request.form['difficulty']
    language = model.get_language(user_name)
    phrase = model.get_phrase_with_difficulty(language, difficulty, request.form['word_number'])
    return jsonify({"word":phrase})


@app.route('/get_score', methods=['POST'])
def get_score():
    user_name = request.form['user']
    score, total_words = get_score(user_name)
    return jsonify({"score": score, "total_words":total_words})


if __name__ == "__main__":
    app.run(debug = True, host = "0.0.0.0", port = 5754)
