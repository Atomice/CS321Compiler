#Jason Hannan
import sys
import socket
import select
from parse import parse_cmd
from parse import parse_var
from parse import parse_text

#Client
#Client Start up
#python Client.py server_address server_port_number
#localhost 2000 example

#Room Variables
rList = []
cRoom = ''

#socket setup
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.settimeout(2)
#server address

#host = sys.argv[1] 
#port address
#port = int(sys.argv[2])
#FOR TESTING
PORT = int(2000)
HOST = 'localhost'


try:
    s.connect((HOST, PORT))
except:
    print 'Error: Can not connect: ' + str(HOST) + ':' + str(PORT)
    sys.exit()

#default room Setup
cRoom = '-Global'
rList.append('-Global')


#Start Communicating!
sys.stdout.write('<---: ')
sys.stdout.flush()


#Channel Communication
while 1:
    socket_list = [sys.stdin, s]
         
    #Select Method for reading and writing
    #Tried using Threads but was troublesome. Using what seems like standard
    #notation for select()
    ready_to_read,ready_to_write,in_error = select.select(socket_list , [], [])
         
    for socket in ready_to_read:             
        if socket == s:
            #Receiving Message
            data = socket.recv(4096)
            if not data:
                print '\nDisconnected from chat server'
                sys.exit()
            else:
                #print data
                sys.stdout.write(data)
                sys.stdout.write('<---: ')
                sys.stdout.flush()     
            
        else :
            #End Message Section
            buffer = sys.stdin.readline()
            buffer = buffer[:-1]
            #Parse Message and format for server
            #msg = buffparser(buffer, cRoom, rList)
            cmd = parse_cmd(buffer)
            if cmd == '/logout':
                s.close()
                sys.exit()
            if cmd == '/sroom':
                var = parse_var(buffer)
                if var == []:
                    continue
                cRoom = var[0]
                continue
            
            s.send(buffer)
            sys.stdout.write('<---: ')
            sys.stdout.flush() 
s.close()
