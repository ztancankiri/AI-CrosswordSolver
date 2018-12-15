import sys
from textblob import TextBlob
import ast
from nltk import WordNetLemmatizer



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
        if not phraseplural(str):
            try:
                jsonDict = {"word": str,
                            "plural": TextBlob(str).words[0].pluralize()}
            except:
                pass
            jsonArray.append(jsonDict)
        else:
            jsonDict = {"word": str,
                        "plural": str}
            jsonArray.append(jsonDict)

    print(jsonArray)
    jsonFile.close()
