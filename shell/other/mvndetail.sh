#!/bin/sh
cd /home/yumin/workspace/taobao/detail/
mvn clean:clean
mvn -Dmaven.test.skip=true install
cd deploy
mvn assembly:assembly
cd ..
