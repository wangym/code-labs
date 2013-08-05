#!/bin/sh
#set -x
echo -n "0:login1.cm3\n1:eticket@daily\n2:circuit@daily\n"
read answer
if [ $answer = 0 ]
then
	ssh xuanyin@login1.cm3.taobao.org
elif [ $answer = 1 ]
then
	ssh xuanyin@10.232.101.107
elif [ $answer = 2 ]
then
	ssh xuanyin@10.125.201.210
else
	echo "bye"
	exit 0
fi
