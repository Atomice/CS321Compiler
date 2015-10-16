#Jason Hannan CS494
import socket
import sys
import select
from parse import parse_cmd
from parse import parse_var
from parse import parse_text

#Global Variables
#Static 
HOST = '' 
RECV_BUFFER = 4096 
PORT = 2000

#Dynamic
#Initialize Rooms
ROOM_LIST = [['-Global',[]]]
#SOCKET LIST
SOCKET_LIST = []
#Client-Addr Pairings
CLNT_ADDR = []

#Sends Message to Clients
def send_data(server_s, s, data):
    
    msg_list = []
    
    var = parse_var(data)
    text = parse_text(data)
    
    for room in ROOM_LIST:
        for vars in var:
            if room[0] == vars:
                msg_list.append(room)
    
    #TESTING
    #print('\n')
    #print(msg_list)
    #print('\n')
    
    if msg_list == []:
        #print(0)
        print('\n')
        print(SOCKET_LIST)
        print('\n')
        print(ROOM_LIST)
        print('\n')
        #All Spam
        for socket in SOCKET_LIST:
            if socket != server_s and socket != s:
                try:
                    socket.send(text + '\n')
                except:
                    #Broken Connection
                    socket.close()
                    if socket in SOCKET_LIST:
                        SOCKET_LIST.remove(socket)
                    for socket_list in ROOM_LIST:
                        if socket in socket_list[1]:
                            socket_list[1].remove(socket)
                    
    else:
        #Message to Room
        for socket_group in msg_list:
            for socket in socket_group[1]:
                if socket != server_s and socket != s:
                    try:
                        socket.send(socket_group[0] + ': ' + text + '\n')
                    except:
                        #Broken Connection
                        socket.close()
                        if socket in SOCKET_LIST:
                            SOCKET_LIST.remove(socket)
                        for socket_list in ROOM_LIST:
                            if socket in socket_list[1]:
                                socket_list[1].remove(socket)
                                
#join room function
def join_room(sock, data):
    var = parse_var(data)
    
    if var == []:
        try:
            sock.send('Server: Did not give a room.\n')
        except:
            #Broken Connection
            sock.close()
            if sock in SOCKET_LIST:
                SOCKET_LIST.remove(sock)
            for socket_list in ROOM_LIST:
                if sock in socket_list[1]:
                    socket_list[1].remove(sock)
        return
                    
    else:
        #Found Room
        for rm in ROOM_LIST:
            if rm[0] == var[0]:
                rm[1].append(sock)
                try:
                    sock.send('Server: Join room \n')
                    return
                except:
                    #Broken Connection
                    sock.close()
                    if sock in SOCKET_LIST:
                        SOCKET_LIST.remove(sock)
                    for socket_list in ROOM_LIST:
                        if sock in socket_list[1]:
                            socket_list[1].remove(sock)
                    return
                
            
        #no room present
        create_room(sock, data)

#create room function
def create_room(sock, data):
    var = parse_var(data)
    
    if var == []:
        try:
            sock.send('Server: Did not give a room.\n')
        except:
            #Broken Connection
            sock.close()
            if sock in SOCKET_LIST:
                SOCKET_LIST.remove(sock)
            for socket_list in ROOM_LIST:
                if sock in socket_list[1]:
                    socket_list[1].remove(sock)
            return
                    
                    
    else:
        #Checks for the room
        for rm in ROOM_LIST:
            if rm[0] == var[0]:
                #Room Found
                join_room(sock, data)
                return
        
        #No room present
        ROOM_LIST.append([var[0],[sock]])
        try:
            sock.send('Server: Created room \n')
        except:
            #Broken Connection
            sock.close()
            if sock in SOCKET_LIST:
                SOCKET_LIST.remove(sock)
            for socket_list in ROOM_LIST:
                if sock in socket_list[1]:
                    socket_list[1].remove(sock)
            return
                        

#leave room function
def leave_room(sock, data):
    var = parse_var(data)
    
    if var == []:
        try:
            sock.send('Server: Did not give a room. \n')
        except:
            #Broken Connection
            sock.close()
            if sock in SOCKET_LIST:
                SOCKET_LIST.remove(sock)
            for socket_list in ROOM_LIST:
                if sock in socket_list[1]:
                    socket_list[1].remove(sock)
    else:
        for rm in ROOM_LIST:
            if rm[0] == var[0]:
                rm[1].remove(sock)
                try:
                    sock.send('Server: Left Room \n')
                except:
                    #Broken Connection
                    sock.close()
                    if sock in SOCKET_LIST:
                        SOCKET_LIST.remove(sock)
                    for socket_list in ROOM_LIST:
                        if sock in socket_list[1]:
                            socket_list[1].remove(sock)
                    return

#List all channels function
def channels(sock):
    print(ROOM_LIST)
    for rm in ROOM_LIST:
        try:
            sock.send('Server:' + str(rm[0]) + '\n')
        except:
            #Broken Connection
            sock.close()
            if sock in SOCKET_LIST:
                SOCKET_LIST.remove(sock)
            for socket_list in ROOM_LIST:
                if sock in socket_list[1]:
                    socket_list[1].remove(sock)
            return

#List all memebers of a channel
def members(sock, data):
    var = parse_var(data)
    
    if var == []:
        try:
            sock.send('Server: Did not give a room. \n')
        except:
            #Broken Connection
            sock.close()
            if sock in SOCKET_LIST:
                SOCKET_LIST.remove(sock)
            for socket_list in ROOM_LIST:
                if sock in socket_list[1]:
                    socket_list[1].remove(sock)
        return
    
    for rm in ROOM_LIST:
        if rm[0] == var[0]:
            for s in rm[1]:
                try:
                    for pair in CLNT_ADDR:
                        if pair[0] == s:
                            addr = pair[1]
                            sock.send('Server: Client [%s, %s] \n' % addr)
                except:
                    #Broken Connection
                    sock.close()
                    if sock in SOCKET_LIST:
                        SOCKET_LIST.remove(sock)
                    for socket_list in ROOM_LIST:
                        if sock in socket_list[1]:
                            socket_list[1].remove(sock)
                    return
        
#Server
#start up:
#python Server.py server_port_number

def server():


    server_s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_s.bind((HOST, PORT))
    server_s.listen(10) 
 
    #Add server socket object to the list of readable connections
    SOCKET_LIST.append(server_s)
 
    print "Chat server started on port " + str(PORT)
 
    while 1:

        #Select Method for reading and writing
        #Tried using Threads but was troublesome. Using what seems like standard
        #notation for select()
        ready_to_read,ready_to_write,in_error = select.select(SOCKET_LIST,[],[],0)
      
        for sock in ready_to_read:
            #New connection
            if sock == server_s: 
                c, addr = server_s.accept()
                #Client Addr pairs
                CLNT_ADDR.append([c,addr])
                SOCKET_LIST.append(c)
                #Room Management
                #Adding user to global room
                ROOM_LIST[0][1].append(c)
                print "Client (%s, %s) connected" % addr
                 
                send_data(server_s, c, "[%s:%s] entered our chatting room\n" % addr)
             
            # Message from a client
            else:
                #Process message 
                try:
                    data = sock.recv(RECV_BUFFER)
                    if data:
                        #Data in socket
                        #Parse message for command
                        cmd = parse_cmd(data)
                        
                        if cmd == '/join':
                            join_room(sock, data)
                        elif cmd == '/create':
                            create_room(sock, data)
                        elif cmd == '/leave':
                            leave_room(sock, data)
                        elif cmd == '/channels':
                            channels(sock)
                        elif cmd == '/members':
                            members(sock, data)
                        elif cmd == '/msg':
                            send_data(server_s, sock, data)
                        else:
                            #Transmit Default Channel
                            send_data(server_s, sock, data)  
                    else:
                        #Broken Socket    
                        if sock in SOCKET_LIST:
                            SOCKET_LIST.remove(sock)
                            
                        for socket_list in ROOM_LIST:
                            if socket in socket_list[1]:
                                socket_list[1].remove(socket)

                        #Highly likely broken link
                        send_data(server_s, sock, "Client (%s, %s) is offline\n" % addr) 
                        
                except:
                    send_data(server_s, sock, "Client (%s, %s) is offline\n" % addr)
                    continue

    server_s.close()


#runs server
if __name__ == "__main__":
    sys.exit(server())   
