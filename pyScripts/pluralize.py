import sys
from textblob import TextBlob
import ast
import subprocess

if len(sys.argv) > 0:

    data = ast.literal_eval(sys.argv[1])
    jsonArray = []
    for str in data:
        if subprocess.Popen(["python3", "isPlural.py", str], stdout=subprocess.PIPE) == "false":
            jsonDict = {"word": str,
                        "plural": TextBlob(str).words[0].pluralize()}
            jsonArray.append(jsonDict)
        else:
            jsonDict = {"word": str,
                        "plural": str}
            jsonArray.append(jsonDict)

    print(jsonArray)
