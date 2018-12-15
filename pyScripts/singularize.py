import sys
import json
from textblob import TextBlob

if len(sys.argv) == 2:

    word = sys.argv[1]
    singular = TextBlob(word).words[0].singularize()

    jsonDict = {"word": word,
                "singular": singular}

    jsonDump = json.dumps(jsonDict)
    print(jsonDump)