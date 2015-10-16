#! /usr/bin/env python


import string
import random

class inputGenerator():
    def __init__(self):
        self.inputList = []
        return

    def make(self,num_strings):
        for i in xrange(num_strings):
            #genereate strings, append to list
            tmp = self.generateString()
            self.inputList.append(tmp)

        return self.inputList
 
    def generateString(self):
        return (''.join(random.choice(string.ascii_uppercase) for i in range(9)))
