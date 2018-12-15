import sys
from nltk.corpus import wordnet
import json

if len(sys.argv) == 2:
    word = sys.argv[1]
    synonyms = []

    for syn in wordnet.synsets(word):
        for l in syn.lemmas():
            synonyms.append(l.name())

    jsonDict = {"word": sys.argv[1], "synonyms": []}


    for synonym in synonyms:
        jsonDict["synonyms"].append(synonym)

    jsonDump = json.dumps(jsonDict)
    print(jsonDump)
