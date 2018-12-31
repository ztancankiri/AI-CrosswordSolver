import sys
import spacy

string = ""
for str in sys.argv:
    string = string + " " + str

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
        print("false")
    else:
        print("true")