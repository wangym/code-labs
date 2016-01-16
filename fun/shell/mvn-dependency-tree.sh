#!/bin/sh
#set -x
rm tree
mvn dependency:tree > tree
vim tree
