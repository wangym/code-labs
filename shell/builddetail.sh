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

sudo rm -rf ~/workspace/taobao/detail/web/target/exploded/detail.war;cd ~/workspace/taobao/detail;mvn clean:clean;mvn -Dmaven.test.skip=true install;cd deploy;mvn assembly:assembly;cd target/;cd ~/workspace/taobao/detail;
