#!/bin/bash
CURRENT_DIR=`pwd`

NAME='jcondor'
rm -rf $NAME

VERSION='1.2'
PACKAGE=${NAME}'-'${VERSION}'.tar.gz'

mkdir $NAME
cp -f jcondor.jar  $NAME/
cp -rf doc         $NAME/
cp -f condor.sh    $NAME/
cp -f README       $NAME/
mkdir -p $NAME/current
chmod -R 755 $NAME/
tar -cvzf $PACKAGE $NAME
chmod 755 $PACKAGE
echo $PACKAGE" done"
# rm -rf $NAME
