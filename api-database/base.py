from flask import Flask
import model
app = Flask(__name__)


@app.route('/get_word/<language>')
def get_word(language):
    word = model.get_word(language)
    return word

@app.route('/get_word/<language>/<difficulty>')
def get_word_with_difficulty(language, difficulty):
    word = model.get_word_with_difficulty(language, difficulty)
    return word

@app.route('/get_phrase/<language>/<difficulty>')
def get_phrase_with_difficulty(language, difficulty):
    phrase = model.get_phrase_with_difficulty(language, difficulty)
    return phrase
