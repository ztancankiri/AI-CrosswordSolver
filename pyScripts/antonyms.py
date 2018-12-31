import sys
from nltk.corpus import wordnet
import ast

if len(sys.argv) > 0:
    antonyms = []
    jsonFile = open(sys.argv[1],"r")
    dataStr = jsonFile.read()
    data = ast.literal_eval(dataStr)
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
    jsonFile.close()
