import sys
from textblob import TextBlob
import ast
import json

if len(sys.argv) > 0:
    jsonFile = open(sys.argv[1],"r")
    dataStr = jsonFile.read()
    data = ast.literal_eval(dataStr)
    jsonArray = []
    for str in data:
        try:
            jsonDict = {"word": str,
                        "singular": TextBlob(str).words[0].singularize().upper()}
        except:
            pass

        jsonArray.append(jsonDict)

    outFile = open("outJSON.txt", "w")
    json.dump(jsonArray, outFile)
    jsonFile.close()
    outFile.close()