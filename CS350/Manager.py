#! /usr/bin/env python
from InputGenerator import inputGenerator
from HashTable import HashTable
from BST import bst
import twothreefour
import random

class Worker():
    def __init__(self):
        self.random_strings = []
        self.gen = inputGenerator()
        self.test_strings = []
        self.error_strings = []

    def genStrings(self,count):
        """makes a list of strings for later use"""
        self.random_strings = self.gen.make(count) #generates 5000 unique strings
        return

    def cherryPick(self,count):
        """returns list of indices of random selected for testing"""
        #for i in range(count):
        #    random_list.append(random.randint(1,stringcount))

        self.test_strings =  random.sample(self.random_strings, count) #how random is this?
        return

    def genErrorString(self):
       
        gen_string = self.gen.generateString()
        while(gen_string in self.random_strings):
            #try again
            gen_string = self.gen.generateString()

        #now we have the string we want
        return gen_string

    def genErrorStringList(self,count):
        for i in range(count):
            tmp = self.genErrorString()
            self.error_strings.append(tmp)

        return

    def build(self,string_count,valid_test_count,invalid_test_count):
        self.genStrings(string_count)
        self.cherryPick(valid_test_count)
        self.genErrorStringList(invalid_test_count)
        return

def restoreInput(filename):
    import pickle
    f = open(filename, "rb")
    tmp = pickle.load(f)
    f.close()
    return tmp

#-----TEST 1-----#
def test(searchcount,invalidcount,stringcount):

        #now run this
        w = Worker()
        search_item_count = searchcount
        invalid_item_count = invalidcount
        string_count = stringcount
        w.build(string_count,search_item_count,invalid_item_count)

        #Save input to pickle file
        import time,pickle
        datadir = "data"
        filetime = str(stringcount)
        inputfilename = datadir+"/"+filetime+"/"+"input-"+filetime
        import os
        if not os.path.exists(os.path.dirname(inputfilename)):
            os.makedirs(os.path.dirname(inputfilename))

        inputfile = open(inputfilename,"w+")
        pickle.dump(w,inputfile)
        inputfile.close()

        #restore = restoreInput(inputfilename)

        trial_count = 10

        #------HASH TABLE------#
        #now we insert
        print("Adding items to hash table....")
        h = HashTable(57)
        import sys
        import time

        datafilename = datadir+"/"+filetime+"/"+"HT-"+filetime+".csv"        
        import os
        if not os.path.exists(os.path.dirname(datafilename)):
          os.makedirs(os.path.dirname(datafilename))
        datafile = open(datafilename,"wb+")
        #prep data file
        datafile.write("\n\nTest "+"SearchCount: "+str(searchcount)+" Invalid Count: "+str(invalidcount)+" StringCount: "+str(stringcount)+"\n")
        datafile.write("HashTable-"+filetime+"-Insert\n")
        datafile.write("Trial,N,BasicOps,Time\n")
        
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")
          h = HashTable(57)
          basicop = 0
          begintime = time.time()
          for i in w.random_strings:
            basicop+=h.add(i)
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("insert time: %s" % diff)
          print("basic op count: %s" % basicop)

        #search trials
        datafile.write("\n\nHashTable-"+filetime+"-SearchSuccessful\n")
        datafile.write("Trial,N,BasicOps,Time\n")
 
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")

          basicop = 0
          begintime = time.time()
          for i in w.test_strings:
            ret=h.search(i)
            basicop+=ret[0]
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("search time: %s" % diff)
          print("basic op count: %s" % basicop)

        datafile.write("\n\nHashTable-"+filetime+"-SearchInvalidStrings\n")
        datafile.write("Trial,N,BasicOps,Time\n")
 
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")

          basicop = 0
          begintime = time.time()
          for i in w.error_strings:
            ret=h.search(i)
            basicop+=ret[0]
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("invalid search time: %s" % diff)
          print("basic op count: %s" % basicop)

        #-----BST--------------#

        datafilename = datadir+"/"+filetime+"/"+"BST-"+filetime+".csv"        
        import os
        if not os.path.exists(os.path.dirname(datafilename)):
          os.makedirs(os.path.dirname(datafilename))
        datafile = open(datafilename,"wb+")
        #prep data file
        datafile.write("\n\nTest "+"SearchCount: "+str(searchcount)+" Invalid Count: "+str(invalidcount)+" StringCount: "+str(stringcount)+"\n")
        datafile.write("BST-"+filetime+"-Insert\n")
        datafile.write("Trial,N,BasicOps,Time\n")
        
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")
          b = bst()
          basicop = 0
          begintime = time.time()
          for i in w.random_strings:
            basicop+=b.insert(i)
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("insert time: %s" % diff)
          print("basic op count: %s" % basicop)

        #search trials
        datafile.write("\n\nBST-"+filetime+"-SearchSuccessful\n")
        datafile.write("Trial,N,BasicOps,Time\n")
 
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")

          basicop = 0
          begintime = time.time()
          for i in w.test_strings:
            ret=b.search(i)
            basicop+=ret[0]
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("search time: %s" % diff)
          print("basic op count: %s" % basicop)

        datafile.write("\n\nBST-"+filetime+"-SearchInvalidStrings\n")
        datafile.write("Trial,N,BasicOps,Time\n")
 
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")

          basicop = 0
          begintime = time.time()
          for i in w.error_strings:
            ret=b.search(i)
            basicop+=ret[0]
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("invalid search time: %s" % diff)
          print("basic op count: %s" % basicop)


        #-----234treeeeeeeeeee--------------#

        datafilename = datadir+"/"+filetime+"/"+"234-"+filetime+".csv"        
        import os
        if not os.path.exists(os.path.dirname(datafilename)):
          os.makedirs(os.path.dirname(datafilename))
        datafile = open(datafilename,"wb+")
        #prep data file
        datafile.write("\n\nTest "+"SearchCount: "+str(searchcount)+" Invalid Count: "+str(invalidcount)+" StringCount: "+str(stringcount)+"\n")
        datafile.write("234-"+filetime+"-Insert\n")
        datafile.write("Trial,N,BasicOps,Time\n")
        
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")
          root = twothreefour.tNode(None)
          tree = twothreefour.Tree(root)
          basicop = 0
          begintime = time.time()
          for i in w.random_strings:
            basicop+=tree.insert(i,root)
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("insert time: %s" % diff)
          print("basic op count: %s" % basicop)

        #search trials
        datafile.write("\n\n234-"+filetime+"-SearchSuccessful\n")
        datafile.write("Trial,N,BasicOps,Time\n")

        #>>> root = twothreefour.tNode(None)
        #>>> root
        #<twothreefour.tNode instance at 0x10e3547a0>
        #>>> tree = twothreefour.Tree(root)
        #>>> tree
        #<twothreefour.Tree instance at 0x10e354758>
        #>>> tree.insert("DERPEPRP",root)
 
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")

          basicop = 0
          begintime = time.time()
          for i in w.test_strings:
            basicop+=tree.search(root,i)[0]
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("search time: %s" % diff)
          print("basic op count: %s" % basicop)

        datafile.write("\n\n234-"+filetime+"-SearchInvalidStrings\n")
        datafile.write("Trial,N,BasicOps,Time\n")
 
        for trial in range(trial_count):
          datafile.write(str(trial+1)+",")

          basicop = 0
          begintime = time.time()
          for i in w.error_strings:
            basicop+=tree.search(root,i)[0]
          endtime = time.time()
          diff = endtime - begintime
          datafile.write(str(stringcount)+",")
          datafile.write(str(basicop)+",")
          datafile.write(str(diff)+"\n")
          print("invalid search time: %s" % diff)
          print("basic op count: %s" % basicop)

        datafile.close()



test(1,1,10)
test(10,10,100)
test(100,100,1000)
test(1000,1000,10000)
test(10000,10000,100000)
test(100000,100000,1000000)
