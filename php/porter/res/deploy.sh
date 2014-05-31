#!/bin/bash 
set -o nounset

SRC_DIR="~/source/github/labs/php/porter"
DST_DIR="~/source/yun/bae/porter"

cp -r ./ $DST_DIR
cd $DST_DIR
pwd
git status
git add *
git commit -m"update"
git push
cd $SRC_DIR
