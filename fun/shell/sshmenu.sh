#!/bin/sh
#set -x
echo "1:login1.cm3"
read answer
if [ $answer = 1 ]
then
	ssh 
elif [ $answer = 2 ]
then
	echo "2 is empty"
else
	echo "bye"
	exit 0
fi

