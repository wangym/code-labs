#!/bin/sh
#set -x
echo -n "1.eticket@daily\n2.eticket@prod\n"
read answer
if [ $answer = 1 ]
then
	ssh xuanyin@10.232.101.107
elif [ $answer = 2 ]
then
	ssh xuanyin@login1.cm3.taobao.org
else
	echo "bye"
	exit 0
fi
