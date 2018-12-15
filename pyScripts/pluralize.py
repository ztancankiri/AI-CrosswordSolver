import sys
import json
from textblob import TextBlob

if len(sys.argv) == 2:

    word = sys.argv[1]
    plural = TextBlob(word).words[0].pluralize()

    jsonDict = {"word": word,
                "plural": plural}

    jsonDump = json.dumps(jsonDict)
    print(jsonDump)