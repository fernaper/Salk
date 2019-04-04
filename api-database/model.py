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


def get_word(language):
    query = {
    "query": {
        "function_score" : {
          "query" : { "match": {"lang": language}},
          "random_score" : {}
        }
      },
     "size":1

    }

    res = es.search(index="words", body=query)
    return res['hits']['hits'][0]['_source']
    



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
    return res['hits']['hits'][0]['_source'] 

#TODO
def get_phrase_with_difficulty(language, difficulty):
    pass


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


#TODO (optional)
def set_difficulty(word):
    #Assign a difficulty regarding a different parameter (not the length)
    pass

#TODO
'''def gendata():
    mywords = ['foo', 'bar', 'baz']
    for word in mywords:
        yield {
            "_index": "mywords",
            "_type": "document",
            "doc": {"word": word},
        }
    bulk(es, gendata())
'''
