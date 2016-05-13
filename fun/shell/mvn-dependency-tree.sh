#!/bin/sh
#set -x
export MAVEN_OPTS=-Xmx1024m
rm tree
mvn dependency:tree > tree
vim tree
