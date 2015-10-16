#! /usr/bin/env python

class HashTable():
    def __init__(self,length):
        self.table = []
        self.length = length
        for i in xrange(self.length):
            self.table.append([])

    def modhash(self,string):
        """string input is len(9), this is the basic op"""
        sum = 0
        for i in string: #this is basic op
            sum+=ord(i)  #we do this 9 times since len(string) == 9
        return (sum % self.length)

    def add(self,string):
        tmphash = self.modhash(string)
        self.table[tmphash].append(string)
        return 9

    def search(self,string):
        retlist = []
        tmphash = self.modhash(string)
        for element in self.table[tmphash]:
            if (string == element):
              retlist.append(9)
              retlist.append(True)
              return retlist
        #Didn't find it
        retlist.append(9)
        retlist.append(False)
        return retlist

    def pprint(self):
        for row in self.table:
            length = len(row)
            if(length > 8):
                #short print
                import sys
                for i in range(7):
                    sys.stdout.write("["+str(row[i])+"] ")

                sys.stdout.write("[...] ")
                sys.stdout.write("["+str(row[-1])+"]\n")
            else:
                print row


