#!/usr/bin/python

import socket
import json
from nltk.corpus import wordnet

s = socket.socket()
host = "localhost"
port = 4444
s.bind((host, port))

print("Server started...")

isActive = True
s.listen(1)
while isActive:
    client, addr = s.accept()
    print("Got connection from " + str(addr))
    
    isConnected = True
    while isConnected:
        data = client.recv(1024)
        jsonObj = json.loads(data)
        
        print(jsonObj)
    
        cmd = jsonObj["cmd"]
        
        if cmd == "closeModule":
            isConnected = False
            isActive = False
        elif cmd == "synonyms":
            word = jsonObj["word"]
            synonyms = []
            
            for syn in wordnet.synsets(word):
                for l in syn.lemmas():
                    synonyms.append(l.name().replace("_", " "))
        
            synonyms = set(synonyms)
            synonyms = list(synonyms)
            
            outputJSON = {
                "cmd": "synonymsResponse",
                "word": word,
                "results": synonyms
            }
            
            output = json.dumps(outputJSON) + "\n"
        
            print(output)
            client.send(output.encode())
            
        elif cmd == "antonyms":
            word = jsonObj['word']
            antonyms = []
    
            for syn in wordnet.synsets(word):
                for l in syn.lemmas():
                    if l.antonyms():
                        antonyms.append(l.antonyms()[0].name().replace("_", " "))
    
            antonyms = set(antonyms)
            antonyms = list(antonyms)
    
            outputJSON = {
                "cmd": "antonymsResponse",
                "word": word,
                "results": antonyms
            }
    
            output = json.dumps(outputJSON) + "\n"
        
            print(output)
            client.send(output.encode())

    client.close()