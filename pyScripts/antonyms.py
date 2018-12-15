import sys
from nltk.corpus import wordnet
import ast

if len(sys.argv) > 0:
    antonyms = []
    data = ast.literal_eval(sys.argv[1])
    jsonArray = []

    for str in data:

        for syn in wordnet.synsets(str):
            for l in syn.lemmas():
                if l.antonyms():
                    antonyms.append(l.antonyms()[0].name())
        jsonDict = {"word": str, "antonyms": []}


        for antonym in antonyms:
            jsonDict["antonyms"].append(antonym)
        jsonArray.append(jsonDict)
        antonyms = []

    print(jsonArray)
