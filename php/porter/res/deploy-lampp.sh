#!/bin/bash 
set -o nounset

SRC_DIR="/home/yumin/source/github/labs/php/porter"
DST_DIR="/opt/lampp/htdocs/"

cd ${DST_DIR}
rm -rf porter
cp ${SRC_DIR} ${DST_DIR} -R
cd ${SRC_DIR}

