#!/usr/bin/python

import sys
import socket


if len(sys.argv) != 3:
  print '\nSyntax: %s <work> <port>\n\n' % sys.argv[0]
  sys.exit(-1)


file = sys.argv[1]
port = int(sys.argv[2])


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind( ('', port) )
s.listen(1)


counter = 0;
start = 0;
f = open(file)

for command in f.readlines():
  if counter >= start:
    conn, addr = s.accept()
    #print 'Connected by', addr
    conn.send(command)
    conn.close()
    print counter
  counter += 1;

s.close()


