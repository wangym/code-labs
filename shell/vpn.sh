#!/bin/bash - 
#===============================================================================
#
#          FILE: vpn.sh
# 
#         USAGE: ./vpn.sh 
# 
#   DESCRIPTION: 
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: YOUR NAME (), 
#  ORGANIZATION: 
#       CREATED: 2013��09��01�� 11ʱ24��33�� CST
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

sudo /usr/bin/openconnect -u xuanyin --script=/etc/vpnc/vpnc-script --no-dtls vpn.alibaba-inc.com
