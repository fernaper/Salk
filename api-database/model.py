from datetime import datetime
from elasticsearch import Elasticsearch
es = Elasticsearch(
    ['localhost'],
    http_auth=('ubuntu', 'salkthebest'),
    scheme="http",
    port=9200,
)


def get_word():
    pass


def get_word_with_difficulty(difficulty):
    pass


def get_phrase_with_difficulty(difficulty):
    pass
