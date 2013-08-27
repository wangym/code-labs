#!/bin/bash - 
#===============================================================================
#
#          FILE: package.sh
# 
#         USAGE: ./package.sh 
# 
#   DESCRIPTION: 
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: YOUR NAME (), 
#  ORGANIZATION: 
#       CREATED: 2013年08月27日 10时53分57秒 CST
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

cd ~/workspace/taobao/detail;mvn clean:clean;mvn -Dmaven.test.skip=true install;cd deploy;mvn assembly:assembly;cd target/;tar -zxvf detail.tar.gz;cd ~/workspace/taobao/detail;
