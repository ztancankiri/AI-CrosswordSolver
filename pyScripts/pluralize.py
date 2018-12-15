import sys
from textblob import TextBlob
import ast
from nltk import WordNetLemmatizer
import json

def phraseplural(string):
    wnl = WordNetLemmatizer()
    lemma = wnl.lemmatize(str, "n")
    if lemma is not str:
        return True
    else:
        return False



if len(sys.argv) > 0:
    jsonFile = open(sys.argv[1],"r")
    dataStr = jsonFile.read()
    data = ast.literal_eval(dataStr)
    jsonArray = []
    for str in data:
        if phraseplural(str) == True:
            try:
                jsonDict = {"word": str,
                            "plural": TextBlob(str).words[0].pluralize().upper()}
            except:
                pass
            jsonArray.append(jsonDict)
        else:
            jsonDict = {"word": str,
                        "plural": str}
            jsonArray.append(jsonDict)

    outFile = open("outJSON.txt", "w")
    json.dump(jsonArray, outFile)
    jsonFile.close()
    outFile.close()
