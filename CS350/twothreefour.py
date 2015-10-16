#########################################
###### Will Oberfest - March 2015 #######
#########################################
import random  # only used for debugging

class tNode:
    def __init__(self, key, lst=None, lmst=None, rmst=None, rst=None, parent=None):
        self.keys = [key, None, None]
        self.children = [lst, lmst, rmst, rst]
        self.parent = None
        
    def isOrphan(self):
        if self.parent is not None:
            return False
        else:
            return True
    
    def sizeOf(self):
        i = 0
        for j in range(len(self.keys)):
            if self.keys[j] != None:
                i = i + 1
        return i    
    
    def isLeaf(self):
        for child in self.children:
            if child is not None:
                return False
        return True
    
# More debug stuff
    def printNode(self):
        for key in self.keys:
            if key != None:
                print key,
class Tree:
    def __init__(self, root):
        self.root = root
    
    def nodeSort(self, root):
        temp = []
        for i in root.keys:
            if i is not None:
                temp.append(i)
        temp.sort() 
        for i in range(3 - len(temp)):
            temp.append(None)
        return temp
            
    def insert(self, key, root):
        count = 0
        current = root        
        # empty node, no keys currently
        if current.sizeOf() == 0:
            current.keys[0] = key
            return count + 1
        # currently at 2 slot node    
        elif current.sizeOf() == 1:         
            if current.isLeaf():
                current.keys[1] = key
                current.keys = self.nodeSort(current)
                return count + 1
            else:
                if key < current.keys[0]:
                    count = self.insert(key, current.children[0])
                    return count + 1
                        
                else: 
                    count = self.insert(key, current.children[1])
                    return count + 1
        # currently at 3 slot node
        elif current.sizeOf() == 2:
            # currently at a leaf (has no children)          
            if current.isLeaf():
                current.keys[2] = key
                current.keys = self.nodeSort(current)
                return count + 1
            # currently NOT at leaf: children exist
            else:
                if key < current.keys[0]:
                    self.insert(key, current.children[0])
                    return count + 1
                elif key < current.keys[1]:
                    count = self.insert(key, current.children[1])
                    return count + 1
                elif key > current.keys[2]:  
                    count = self.insert(key, current.children[2]) 
                    return count + 1

        # finally at 4 slot node, gotta split  
        elif current.sizeOf() == 3:             
            # current node is an orphan, has no parents
            if current.isOrphan():       
                tempL = tNode(current.keys[0])
                tempL.keys[1] = tempL.keys[2] = None
                tempL.children[0] = current.children[0]
                tempL.children[1] = current.children[1]
                tempL.children[2] = tempL.children[3] = None
                tempL.parent = current  
                tempR = tNode(current.keys[2])
                tempR.keys[1] = tempL.keys[2] = None
                tempR.children[0] = current.children[2]
                tempR.children[1] = current.children[3]
                tempR.children[2] = tempR.children[3] = None
                tempR.parent = current                            
                current.keys[0] = current.keys[1]
                current.keys[1] = current.keys[2] = None
                current.children[0] = tempL
                current.children[1] = tempR
                
                current.parent = None
                current.children[2] = None
                current.children[3] = None     
                count = self.insert(key, self.root)
                return count + 1
            # current node's parent has ONE item in it:
            elif current.parent.sizeOf() == 1:                         
                # if current is a LST
                if current.parent.children[0] == current:          
                    current.parent.keys[1] = current.keys[1]
                    current.parent.keys = self.nodeSort(current.parent)
                    current.parent.children[2] = current.parent.children[1]
                    current.parent.children[1] = tNode(current.keys[2], current.children[2], current.children[3], None, None, current.parent)
                    current.parent.children[0] = tNode(current.keys[0], current.children[0], current.children[1], None, None, current.parent) 
                    count = self.insert(key, self.root)
                    return count + 1 
                    
                # else if current is a RST
                elif current.parent.children[1] == current:               
                    current.parent.keys[1] = current.keys[1]
                    current.parent.keys = self.nodeSort(current.parent)
                    current.parent.children[2] = tNode(current.keys[2], current.children[2], current.children[3], None, None, current.parent)
                    current.parent.children[1] = tNode(current.keys[0], current.children[0], current.children[1], None, None, current.parent)                  
                    count = self.insert(key, self.root)
                    return count + 1
                    
            # current node's parent has TWO items in it:        
            elif current.parent.sizeOf() == 2:
                # if current is LST
                if current.parent.children[0] == current: 
                    current.parent.keys[2] = current.keys[1]
                    current.parent.keys = self.nodeSort(current.parent)
                    current.parent.children[3] = current.parent.children[2]
                    current.parent.children[2] = current.parent.children[1]
                    current.parent.children[1] = tNode(current.keys[2], current.children[2], current.children[3], None, None, current.parent)
                    current.parent.children[0] = tNode(current.keys[0], current.children[0], current.children[1], None, None, current.parent)
                    count = self.insert(key, self.root)
                    return count + 1
                
                # else if MST
                elif current.parent.children[1] == current:
                    current.parent.keys[2] = current.keys[1]
                    current.parent.keys = self.nodeSort(current.parent)
                    current.parent.children[3] = current.parent.children[2]
                    current.parent.children[2] = tNode(current.keys[2], current.children[2], current.children[3], None, None, current.parent)
                    current.parent.children[1] = tNode(current.keys[0], current.children[0], current.children[1], None, None, current.parent)
                    count = self.insert(key, self.root)        
                    return count + 1
                    
                # else if RST                
                elif current.parent.children[2] == current:
                    current.parent.keys[2] = current.keys[1]
                    current.parent.keys = self.nodeSort(current.parent)
                    current.parent.children[3] = tNode(current.keys[2], current.children[2], current.children[3], None, None, current.parent)
                    current.parent.children[2] = tNode(current.keys[0], current.children[0], current.children[1], None, None, current.parent)
                    count = self.insert(key, self.root)
                    return count + 1                                 

    def search(self, node, key):
        current = node
        count = [0, 0]
        if current is None:
            return [count[0] + 1, 0] 
        if current.sizeOf() == 1:
            if current.keys[0] == key:
                return [count[0] + 1, 1]
            elif key < current.keys[0]:
                count = self.search(current.children[0], key)
                return [count[0] + 1, count[1]]
            elif key > current.keys[0]:
                count = self.search(current.children[1], key)
                return [count[0] + 1, count[1]]
        elif current.sizeOf() == 2:
            for i in range (0, 2):
                if current.keys[i] is not None:
                    if current.keys[i] == key:
                        return [count[0] + i + 1, 1]         
            if key < current.keys[0]:
                count = self.search(current.children[0], key)
                return [count[0] + 1, count[1]]
            elif key < current.keys[1]:
                count = self.search(current.children[1], key)
                return [count[0] + 1, count[1]]         
            elif key > current.keys[1]:
                count = self.search(current.children[2], key)
                return [count[0] + 1, count[1]]
            else:
                return [count[0] + 1, 0]                   
            
        elif current.sizeOf() == 3:
            for i in range (0, 3):
                if current.keys[i] is not None:
                    if current.keys[i] == key:
                        return [count[0] + i + 1, 1]        
            if key < current.keys[0]:
                count = self.search(current.children[0], key)
                return [count[0] + 1, count[1]]
            elif key < current.keys[1]:
                count = self.search(current.children[1], key)
                return [count[0] + 1, count[1]]            
            elif key < current.keys[2]:
                count = self.search(current.children[2], key)
                return [count[0] + 1, count[1]]
            elif key > current.keys[2]:
                count = self.search(current.children[3], key)
                return [count[0] + 1, count[1]]
            else:
                return [count[0] + 1, 0]
        else:
            return [count[0] + 1, 0]
        
#########################################################################################
#### Everything below here can be deleted: was used for proving correctness/debugging####
#########################################################################################

def formalPrint(root, formal):
    current = root
    if current is not None:
        formalPrint(current.children[0], formal)
        for i in range (0, 3):
            if current.keys[i] is not None:
                formal.append(current.keys[i])
            formalPrint(current.children[i + 1], formal)        
            
def inorderPrint(root, inorder):
    current = root
    if current is not None:
        inorderPrint(current.children[0], inorder)
        for i in range (0, 3):
            inorder.append(current.keys[i])
            if i == 2:
                inorder.append(".")
            inorderPrint(current.children[i + 1], inorder)

# def testFunc():
#     legend = []
#     for i in range (0, 256):
#         legend.append(i)
#     print legend
#     count = 0
#     completeSorts = 0
#     completeSearches = 0 
#     r = 0
#     while  r < 500:
#         sample = []
#         for i in range (0, 256):
#             sample.append(i)        
#         root = tNode(None)
#         tree = Tree(root)
#         random.shuffle(sample)
#     #    print sample
#         for item in sample:
#             tree.insert(item, root)
#         formal = []
#         formalPrint(root, formal)
#         count = count + 1
#         c = tree.search(root, random.choice(sample))
#         if c[1] != 1:
#             completeSearches = completeSearches + 1
#         if formal != legend:
#             print "Error: ",
#             print formal
#         else:
#             completeSorts = completeSorts + 1
#         r = r + 1
#     print "complete sorts: ",
#     print completeSorts
#     print "failed searches: ",
#     print completeSearches
#     print "total runs: ",
#     print count
    

# def testFunc2():
#     root = tNode(None)
#     tree = Tree(root)
#     
#     sample = [2, 1, 0, 4, 6, 10, 11, 3, 8, 7, 5, 9]
#     sumOps = 0
#     
#     print sample
#     for item in sample:
#         temp = tree.insert(item, root)
#         sumOps = sumOps + temp
#     print "sumOps = ", 
#     print sumOps
#     
#     formal = []
#     formalPrint(root, formal)
#     print formal
#     
#     inorder = []
#     inorderPrint(root, inorder)
#     print inorder

###############
#testFunc()
#testFunc2()
