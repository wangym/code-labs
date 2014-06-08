#!/bin/bash 
set -o nounset

SRC_DIR="/home/yumin/source/github/labs/php/porter"
DST_DIR="/home/yumin/source/yun/bae/porter"

cd ${DST_DIR}
git rm -rf *
git commit -m"delete"
cp -r ${SRC_DIR}/. ${DST_DIR}
git add *
git commit -m"update"
git push
cd ${SRC_DIR}

