from datetime import datetime
import requests
from elasticsearch import Elasticsearch, helpers
es = Elasticsearch(
    ['localhost'],
    scheme="http",
    port=9200,
)


def connection():
    res = requests.get('http://localhost:9200')
    return res


def get_word_with_difficulty(language, difficulty, exclude_words=[]):
    query = {
    "query": {
        "function_score" : {
          "query" : {
            "bool": {
              "must": [
                {
                  "match": {
                    "difficulty": difficulty
                  }
                },
                {
                  "match": {
                    "lang": language
                  }
                }
              ],
              "must_not": [
                {
                    "term" : {"word": exclude_words}
                }
              ]
            }
          },
          "random_score" : {}
        }
      },
     "size":1

     }

    res = es.search(index="words",doc_type='_doc', body=query)
    return res['hits']['hits'][0]['_source']['word']



def get_phrase_with_difficulty(language, difficulty, word_number):
    word_list = ''
    no_word = []
    for i in range(0,word_number):
        word = get_word_with_difficulty(language, difficulty, no_word)
        no_word.append(word)
        word_list += word

    return word_list



def get_language(user_name):
    query = {
        "query": {
            "match": {
                "name": user_name
            }
        }
    }

    res = es.search(index="users",doc_type='_doc', body=query)
    return res['hits']['hits'][0]['_source']['language']

def get_difficulty(user_name):
    query = {
        "query": {
            "match": {
                "name": user_name
            }
        }
    }

    res = es.search(index="users",doc_type='_doc', body=query)
    return res['hits']['hits'][0]['_source']['difficulty']


def exist_user(user_name):
    query = {
        "query": {
            "match": {
                "name": user_name
            }
        }
    }

    res = es.search(index="users",doc_type='_doc', body=query)
    return res['hits']['hits'] != []

def insert_language(word_dict, lang):

    actions = [
        {
            "_index": "words",
            "_type": "_doc",
            "_source": {
                "word" : word,
                "lang": lang,
                "difficulty": len(word)}
        }
        for word in word_dict
    ]
    res = helpers.bulk(es, actions)
    return res


def create_user(user_name, language='spanish'):
    query = {
        "name" : user_name,
        "language" : language,
        "difficulty" : 1,
    }

    res = es.index(index="users",doc_type='_doc', body=query)


def set_language(user_name, lang):
    query = {
        "script": {
            "source": "ctx._source.language='{}'".format(lang),
            "lang": "painless"
        },
        "query": {
            "match": {
                "name": user_name
            }
        }
    }

    res = es.update_by_query(index="users",doc_type='_doc', body=query)
    return


def set_difficulty(user_name, difficulty):
    query = {
        "script": {
            "source": "ctx._source.difficulty='{}'".format(difficulty),
            "lang": "painless"
        },
        "query": {
            "match": {
                "name": user_name
            }
        }
    }

    res = es.update_by_query(index="users",doc_type='_doc', body=query)
    return


def get_score(user_name):
    query = {
        "query": {
            "match": {
                "name": user_name
            }
        }
    }

    res = es.search(index="users",doc_type='_doc', body=query)
    hit = res['hits']['hits'][0]['_source']
    easy_level_words = hit['easy_level_words']
    medium_level_words = hit['medium_level_words']
    hard_level_words = hit['hard_level_words']
    total_words = easy_level_words + medium_level_words + hard_level_words
    return easy_level_words, medium_level_words, hard_level_words, total_words
