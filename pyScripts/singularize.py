import sys
from textblob import TextBlob
import ast

if len(sys.argv) > 0:
    jsonFile = open(sys.argv[1],"r")
    dataStr = jsonFile.read()
    data = ast.literal_eval(dataStr)
    jsonArray = []
    for str in data:
        jsonDict = {"word": str,
                    "singular": TextBlob(str).words[0].singularize()}
        jsonArray.append(jsonDict)

    print(jsonArray)