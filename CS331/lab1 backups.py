def factorial(n):
    if n==0:
        return 1
    else:
        return n*factorial(n-1)

def multiples_of_3_and_5(n):
    n-=1
    if n == 0:
        return 0
    if n%3 == 0 or n%5 == 0:
        return n+multiples_of_3_and_5(n)
    else:
        return multiples_of_3_and_5(n)

def integer_right_triangles(p):
    l = []
    sqrt = int(p**2)
    for a in range(1,p+1):
        for b in range(1,p):
            c = ((a**2+b**2)**0.5)
            if a+b+c == p and a<b<c:
                l.append((a,b,(int)(c)))
    return l

def gen_pattern(chars):
    length = len(chars)
    string = ''
    dots = (length*2 - 1)*2 - 1
    if length == 1:
        return chars
    else:
        string = ('.'.join(chars[length-1:0:-1]+chars)).center(dots,'.')
        for index in range(1, length):
            line = ('.'.join(chars[length-1:index-1:-1]+chars[index+1:length])).center(dots,'.')
            string += '\n'+ line
        return string[len(string)-1:dots:-1]+'\n'+string

def gen_pattern_NOT_OPTIMIZED(chars):
    l = list()
    length = len(chars)
    string = ''
    dots = (length*2 - 1)*2 - 1 # number of chars on line
    for x in range(0, length):
        line = ''   # reset line
        string += chars[x]  # add a char to string
        for y in range(0, len(string)):
            if y % 2 != 0:  # autofill a dot at every odd placement
                line += chars[length -1 - y].center(3, '.')
            else:
                line += chars[length - 1 - y] # add a char at every even placement
        for k in range(x - 1, -1, -1): # add other half of line to string
            if k % 2 != 0:
                line += chars[length - 1 - k].center(3, '.')
            else:
                line += chars[length - 1 - k]
        line = line.center(dots, '.') # autofill dots
        l.append(line)
    retval = ''
    if (length > 1):
        for p in range(0,len(l)):
            retval+=l[p]+'\n'
        for q in range(len(l)-2,0,-1):
            retval+=l[q]+'\n'
        retval+=l[0]
        return retval
    else:
        return chars