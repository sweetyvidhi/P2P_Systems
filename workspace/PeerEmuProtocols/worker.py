#!/usr/bin/python

import sys
import os
import socket
from socket import gethostname


if len(sys.argv) != 3:
  print '\nSyntax: %s <server> <port>\n\n' % sys.argv[0]
  sys.exit(-1)


server = sys.argv[1]
port = int(sys.argv[2])

while True:
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((server, port))
    command = s.recv(1024)
    s.close()
    os.system(command)
  except:
    print 'Worker on '+gethostname()+' exited.'
    sys.exit(0)


