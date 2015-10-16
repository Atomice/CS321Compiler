#Jason Hannan


def parse_cmd(str):
    #parses command
    i = 0
    temp = ''
    k = len(str)
    
    if str[i] != '/':
        return ''
    while(i < k):
        if str[i] == '-':
            return temp
        if str[i] == ' ':
            return temp
        else:
            temp = temp + str[i]
        i = i + 1
    
    return temp

def parse_var(str):
    #parses variables
    #local variables
    i = 0
    temp = ''
    tlist = []
    k = len(str)
    
    while(i < k):
        if str[i] != '-':
            i = i + 1
        else:
            temp = '-'
            i = i + 1
            break
    
    #build and return variables
    while(i < k):
        if str[i] == '|':
            tlist.append(temp)
            return tlist
        if str[i] == '-':
            tlist.append(temp)
            temp = ''
        if str[i] == ' ':
            if str[i+1] == '-':
                pass
            else:
                tlist.append(temp)
                return tlist
        else:
            temp = temp + str[i]
        i = i + 1
        
    if temp == '':
        pass
    else:
        tlist.append(temp)
        
    return tlist
    

def parse_text(str):
    #parses out text
    
    #local variables
    i = 0
    temp = ''
    k = len(str)
    
    #find variables in message data
    if str[0] != '/' and str[0] != '-':
        return str
    
    else:
        while(i < k):
            if str[i] == ' ' or str[i] == '|':
                if str[i+1] != '/' and str[i+1] != '-' and str[i+1] != ' ':
                    i = i +1
                    break
            i = i + 1
    
    #build and return variables
    while(i < k):
        temp = temp + str[i]
        i = i + 1
        
    return temp

#state = 'running'

#while(state == 'running'):
#    data = raw_input('Enter Input: ')
#    cmd = parse_cmd(data)
#    var = parse_var(data)
#    text = parse_text(data)
#    print(data + '\n')
#    print(cmd + '\n')
#    print(var)
#    print('\n')
#    print(text + '\n')



