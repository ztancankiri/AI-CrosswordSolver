import sys
from textblob import TextBlob
import ast

if len(sys.argv) > 0:
    jsonFile = open(sys.argv[1],"r")
    dataStr = jsonFile.read()
    data = ast.literal_eval(dataStr)
    jsonArray = []
    for str in data:
        try:
            jsonDict = {"word": str,
                        "singular": TextBlob(str).words[0].singularize()}
        except:
            pass

        jsonArray.append(jsonDict)

    print(jsonArray)
    jsonFile.close()