'''
Created on May 2, 2010

@author: xianthax
'''


import getopt
import sys
import csv


def main (argv):
    
    fName = argv
    
    
            
    f = open('noteDataOut.java','w')
    
    inReader = csv.reader(open('NoteData.csv'), delimiter=';', quotechar='"')
    
    inReader.next()  #skips the column names
   
    #column order
    #0: Index
    #1: Note Name String
    #2: Frequency
    #3: Sample File Number
    #4: Speed
    
        
    #notes.add(new Note("A1",0,0.500000f));
 
    for row in inReader:
        f.write("notes.add(new Note(")
        f.write('"')
        f.write(row[1])
        f.write('",')
        f.write(str(int(row[3]) - 1))
        f.write(",")
        f.write(row[4])
        f.write("f));\n")
    
        
    

if __name__ == '__main__':
    main(sys.argv[1:])