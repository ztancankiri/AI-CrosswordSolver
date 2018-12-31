from nltk.stem import WordNetLemmatizer
import sys
import spacy

string = ""
for str in sys.argv:
    string = string + " " + str


def subjectplural():
    nlp = spacy.load("en")
    doc = nlp(string)
    whl = WordNetLemmatizer()
    object = ""
    for text in doc:
        if text == "iobj" or text == "dobj":
            object = text.orth_

    lemma = whl.lemmatize(object, "n")
    if lemma is not str:
        print("true")
    else:
        print("false")


def phraseplural():
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
        print("true")
    else:
        print("false")


if len(sys.argv) > 0:
    nlp = spacy.load("en")
    doc = nlp(string)
    isSubject = True
    subject = ""
    verb = ""
    for text in doc:
        if text.dep_ == "nsubj":
            subject = text.orth_

        if text.dep_ == "ROOT":
            verb = text.orth_

    if subject is "" or verb is "":
        phraseplural()
    else:
        try:
            subjectplural()
        except:
            print("false")