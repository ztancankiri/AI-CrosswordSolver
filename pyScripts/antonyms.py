import sys
import nltk
from nltk.corpus import wordnet

if len(sys.argv) == 2:
	word = sys.argv[1]
	antonyms = []
	
	for syn in wordnet.synsets(word):
		for l in syn.lemmas():
			if l.antonyms():
				antonyms.append(l.antonyms()[0].name())
	
	antonyms = set(antonyms)
	antonyms = list(antonyms)
	
	outputA = ""
	
	for antonym in antonyms:
		if antonym != antonyms[-1]:
			outputA += antonym.replace("_", " ") + ","
		else:
			outputA += antonym.replace("_", " ")
	
	print(outputA)