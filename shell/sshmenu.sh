#!/bin/sh
#set -x
echo -n "1:login1.cm3\n2:eticket@daily\n3:circuit@daily\n"
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
else
	echo "bye"
	exit 0
fi
