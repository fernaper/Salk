from datetime import datetime
import requests
from elasticsearch import Elasticsearch, helpers
es = Elasticsearch(
    ['localhost'],
    #http_auth=('ubuntu', 'salkthebest'),
    scheme="http",
    port=9200,
)

def connection():
    res = requests.get('http://localhost:9200')
    return res


def get_word_with_difficulty(language, difficulty):
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
    

#TODO
def get_phrase_with_difficulty(language, difficulty):
    pass



def get_user(user_name):
    query = {
        "query": {
            "match": {
                "name": "antonio"
            }
        }
    }

    res = es.search(index="users",doc_type='_doc', body=query)
    return res['hits']['hits'][0]['_source']['lang']


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
