#include "types.h"
#include "stat.h"
#include "fcntl.h"
#include "user.h"
#include "x86.h"

char*
strcpy(char *s, const char *t)
{
  char *os;

  os = s;
  while((*s++ = *t++) != 0)
    ;
  return os;
}

int
strcmp(const char *p, const char *q)
{
  while(*p && *p == *q)
    p++, q++;
  return (uchar)*p - (uchar)*q;
}

uint
strlen(const char *s)
{
  int n;

  for(n = 0; s[n]; n++)
    ;
  return n;
}

void*
memset(void *dst, int c, uint n)
{
  stosb(dst, c, n);
  return dst;
}

char*
strchr(const char *s, char c)
{
  for(; *s; s++)
    if(*s == c)
      return (char*)s;
  return 0;
}

char*
gets(char *buf, int max)
{
  int i, cc;
  char c;

  for(i=0; i+1 < max; ){
    cc = read(0, &c, 1);
    if(cc < 1)
      break;
    buf[i++] = c;
    if(c == '\n' || c == '\r')
      break;
  }
  buf[i] = '\0';
  return buf;
}

int
stat(const char *n, struct stat *st)
{
  int fd;
  int r;

  fd = open(n, O_RDONLY);
  if(fd < 0)
    return -1;
  r = fstat(fd, st);
  close(fd);
  return r;
}

int
atoi(const char *s)
{
  int n;

  n = 0;
  while('0' <= *s && *s <= '9')
    n = n*10 + *s++ - '0';
  return n;
}

uint hextoint(const char* hex){
  uint val = 0; int x=2;
  if(hex[0]!='0'){ return -1; }
  if (hex[1]!='x'){ return -1; }
  while((int)hex[x] && x<10){
    val = val << 4;
    switch(hex[x]){
      case('0'):
      case('1'):
      case('2'):
      case('3'):
      case('4'):
      case('5'):
      case('6'):
      case('7'):
      case('8'):
      case('9'):
        val = (hex[x])-'0';
        break;
        
      case('A'):
      case('B'):
      case('C'):
      case('D'):
      case('E'):
      case('F'):
        val = (uint)(hex[x])-'A'+10;
        break;

      case('a'):
      case('b'):
      case('c'):
      case('d'):
      case('e'):
      case('f'):
        val = (uint)(hex[x])-'a'+10;
        break;
      default:
        return -1;
    }
    x++;
  }
  return val;
}

void*
memmove(void *vdst, const void *vsrc, int n)
{
  char *dst;
  const char *src;

  dst = vdst;
  src = vsrc;
  while(n-- > 0)
    *dst++ = *src++;
  return vdst;
}
