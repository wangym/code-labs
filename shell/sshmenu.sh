#!/bin/sh
#set -x
echo -n "1:login1.cm3\n2:eticket@10.232.101.107\n3:circuit@10.125.201.210\n4:detail@10.235.144.147\n5:detail@10.235.144.148\n"
read answer
if [ $answer = 1 ]
then
	ssh xuanyin@login1.cm3.taobao.org
elif [ $answer = 2 ]
then
	ssh xuanyin@10.232.101.107
elif [ $answer = 3 ]
then
	ssh xuanyin@10.125.201.210
elif [ $answer = 4 ]
then
	ssh xuanyin@10.235.144.147
elif [ $answer = 5 ]
then
	ssh xuanyin@10.235.144.148
else
	echo "bye"
	exit 0
fi
