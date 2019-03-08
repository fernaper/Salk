from flask import Flask
import model
app = Flask(__name__)


@app.route('/get_word')
def get_word():
    word = model.get_word()
    return word

@app.route('/get_word/<difficulty>')
def get_word_with_difficulty(difficulty):
    word = model.get_word_with_difficulty(difficulty)
    return word

@app.route('/get_phrase/<difficulty>')
def get_phrase_with_difficulty(difficulty):
    phrase = model.get_phrase_with_difficulty(difficulty)
    return phrase
