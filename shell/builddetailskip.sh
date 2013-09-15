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

cp /home/yumin/source/labs/other/antx.properties.detailskip ~/antx.properties;sudo rm -rf /home/yumin/workspace/taobao/detailskip/detailskip-web/target/detailskip.war;cd ~/workspace/taobao/detailskip;mvn -Dmaven.test.skip=true clean install;cd deploy;mvn assembly:assembly;cd /home/yumin/workspace/taobao/detailskip/detailskip-web/target;rm -rf detailskip.war;mv detailskip detailskip.war;rm ~/antx.properties
