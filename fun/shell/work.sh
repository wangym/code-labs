#!/bin/sh
#set -x
if [ "$1" = "alibaba" -o "$1" = "yumin" ]
then
	cp ~/.gitconfig.$1 ~/.gitconfig
	cp ~/.m2/settings.xml.$1 ~/.m2/settings.xml
	echo "work at $1 done"
else
	echo "bye"
	exit 0
fi

