#!/usr/bin/env python
#Jason Hannan
#CS350
#Dictionary: Binary Search Tree


class node():
  aKey = None
  left = None
  right = None

  def __init__(self, s):
    self.aKey = s
    self.left = None
    self.right = None
  
  def insert_node(self, s ):
    count = 0
    if s <= self.aKey:
      if self.hasLeftChild():
        count = self.left.insert_node( s )
        return count + 1
      self.left = node(s)
      return 2
    if s > self.aKey:
      if self.hasRightChild():
        count = self.right.insert_node( s )  
        return count + 1
      self.right = node(s)
      return 2
    return 0

  def search_node(self, s ):
    count = [0,0]
    if s == self.aKey:
      return [count[0] + 1, 1]
    if s < self.aKey:
      if self.hasLeftChild():
        count = self.left.search_node( s )
        return [count[0] + 1, count[1]]
      return [count[0] + 1, 0]
    if s > self.aKey:
      if self.hasRightChild():
        count = self.right.search_node( s )
        return [count[0] + 1, count[1]]
      return [count[0] + 1, 0]
 
  def hasRightChild(self):
    return self.right
  def hasLeftChild(self):
    return self.left

class bst():
  
  root = None
  
  def __init__(self):
    self.root = None

  def insert( self, s ):
    if self.hasRoot():
      return self.root.insert_node( s )
    self.root = node(s)
    return 1

  def search(self, s ):
    if self.hasRoot():
      return self.root.search_node( s )
    return "ERROR"
 
  def hasRoot(self):
    return self.root

