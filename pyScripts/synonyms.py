import sys
import nltk
from nltk.corpus import wordnet

if len(sys.argv) == 2:
	word = sys.argv[1]
	synonyms = []
	
	for syn in wordnet.synsets(word):
		for l in syn.lemmas():
			synonyms.append(l.name())
	
	synonyms = set(synonyms)
	synonyms = list(synonyms)
	
	outputS = ""
	
	for synonym in synonyms:
		if synonym != synonyms[-1]:
			outputS += synonym.replace("_", " ") + ","
		else:
			outputS += synonym.replace("_", " ")
	
	print(outputS)