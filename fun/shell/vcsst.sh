#!/bin/bash - 
set -o nounset

# 常量定义:
git=".git"
svn=".svn"

##### function #####
# 函数定义:
function vcs_status()
{
	if [ -d "$git" ]
	then
		git status
	elif [ -d "$svn" ]
	then
		svn st
	else
		echo "unknown!"
	fi
}

##### vcsst.sh #####
clear
if [ ! -d "$git" ] && [ ! -d "$svn" ]
then
	# 当前目录非VCS:
	ls |while read line
	do
		echo "=====["$line"]====="
		cd $line
		vcs_status
		cd ..
	done
else
	# 当前已便是VCS:
	vcs_status
fi

