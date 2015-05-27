#!/bin/bash - 
set -o nounset

# 常量定义:
git=".git"
svn=".svn"

##### function #####
# 函数定义:
function vcs_update()
{
	if [ -d "$git" ]
	then
		git pull; git fetch
	elif [ -d "$svn" ]
	then
		svn up
	else
		echo "unknown!"
	fi
}

##### vcsup.sh #####
clear
if [ ! -d "$git" ] && [ ! -d "$svn" ]
then
	# 当前目录非VCS:
	ls |while read line
	do
		echo "=====["$line"]====="
		cd $line
		vcs_update
		cd ..
	done
else
	# 当前已便是VCS:
	vcs_update
fi

