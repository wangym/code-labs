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
#       CREATED: 2013��08��27�� 10ʱ53��57�� CST
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

cd ~/workspace/taobao/detail;mvn clean:clean;mvn -Dmaven.test.skip=true install;cd deploy;mvn assembly:assembly;cd target/;tar -zxvf detail.tar.gz;cd ~/workspace/taobao/detail;
