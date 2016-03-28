import os
import sys
from datetime import datetime

#msgloss= [1,5,10,15,20,25,30]
#grpsize=[3,4,5,6,7,8,9,10,11]
#for loss in range(len(msgloss)):
#	for gsize in range(len(grpsize)):
#		stt = 'susp_m'+`msgloss[loss]`+'_g_'+`grpsize[gsize]`
#		command = './go src/thesis/three/aggregation.cfg MSGLOSS='+`msgloss[loss]`+' GROUPSIZE='+`grpsize[gsize]`+' >> '+`stt`
#		print command

random=1
i=1
x=[5,10,15,20,25,30,35,40,45,50]
for j in range(len(x)):
	random=1
	for i in range(100):
		command = './go src/thesis/three/aggregation.cfg X='+`x[j]`+' RANDOMSEED='+`random`+' >> accuracy'
		print command
		random=random+5

