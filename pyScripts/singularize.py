import sys
from textblob import TextBlob
import ast

if len(sys.argv) > 0:

    data = ast.literal_eval(sys.argv[1])
    jsonArray = []
    for str in data:
        jsonDict = {"word": str,
                    "singular": TextBlob(str).words[0].singularize()}
        jsonArray.append(jsonDict)

    print(jsonArray)