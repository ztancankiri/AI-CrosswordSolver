import sys
from nltk.corpus import wordnet
import ast

if len(sys.argv) > 0:
    synonyms = []
    jsonFile = open(sys.argv[1],"r")
    dataStr = jsonFile.read()
    data = ast.literal_eval(dataStr)
    jsonArray = []

    for str in data:

        for syn in wordnet.synsets(str):
            for l in syn.lemmas():
                synonyms.append(l.name())

        jsonDict = {"word": str, "synonyms": []}

        for synonym in synonyms:
            jsonDict["synonyms"].append(synonym)

        jsonArray.append(jsonDict)
        synonyms = []

    print(jsonArray)
    jsonFile.close()
