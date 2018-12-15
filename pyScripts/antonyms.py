import sys
from nltk.corpus import wordnet
import json

if len(sys.argv) == 2:
    word = sys.argv[1]
    antonyms = []

    for syn in wordnet.synsets(word):
        for l in syn.lemmas():
            if l.antonyms():
                antonyms.append(l.antonyms()[0].name())

    jsonDict = {"word": sys.argv[1], "antonyms": []}


    for antonym in antonyms:
        jsonDict["antonyms"].append(antonym)

    jsonDump = json.dumps(jsonDict)
    print(jsonDump)
