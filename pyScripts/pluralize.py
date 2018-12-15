import sys
from textblob import TextBlob
import ast
import subprocess
from nltk import WordNetLemmatizer



def phraseplural(string):
    wnl = WordNetLemmatizer()
    lemma = wnl.lemmatize(str, "n")
    if lemma is not str:
        return True
    else:
        return False



if len(sys.argv) > 0:
    data = ast.literal_eval(sys.argv[1])
    jsonArray = []
    for str in data:
        if not phraseplural(str):
            jsonDict = {"word": str,
                        "plural": TextBlob(str).words[0].pluralize()}
            jsonArray.append(jsonDict)
        else:
            jsonDict = {"word": str,
                        "plural": str}
            jsonArray.append(jsonDict)

    print(jsonArray)
