from nltk.stem import WordNetLemmatizer
import sys

if len(sys.argv) > 0:
    subjectList = sys.argv
    wnl = WordNetLemmatizer()
    boolList = []
    for str in subjectList:
        lemma = wnl.lemmatize(str, "n")
        if lemma is not str:
            boolList.append(True)
        else:
            boolList.append(False)

    if True in boolList:
        print("True")
    else:
        print("False")