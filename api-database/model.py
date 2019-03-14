from datetime import datetime
from elasticsearch import Elasticsearch
es = Elasticsearch(
    ['localhost'],
    #http_auth=('ubuntu', 'salkthebest'),
    scheme="http",
    port=9200,
)


def get_word(language):
    query =
    {
    "query": {
        "function_score" : {
          "query" : { "match": {"lang": language}},
          "random_score" : {}
        }
      },
     "size":1

    }

    res = es.search(index="words", body=query)
    print(res)
    return



def get_word_with_difficulty(difficulty):
    query =
    {
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
    print(res)
    return res

#TODO
def get_phrase_with_difficulty(language, difficulty):
    pass


def insert_language(word_dict, lang):
    for word in word_dict:
        insert ={
        "word":word,
        "lang":lang,
        "difficulty": len(word)}
        res = es.index(index='my_index',doc_type='_doc',body=insert)
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
