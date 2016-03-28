#! /bin/bash
if [ $1 -eq 0 ] 
then
	python server.py WORK.txt 8080
else
	echo $PRUN_HOSTNAMES > temp
	MASTER=`grep -o -m 1 'node[0-9]\{3\}' temp | head -1`	
	python worker.py $MASTER 8080
fi

