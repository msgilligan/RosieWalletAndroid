#!/bin/bash

# Source setup.sh
setup_script=""
search_dir=$(dirname $(readlink -f ${BASH_SOURCE[0]}))
cur_dir=${search_dir}

while [[ ${setup_script} == "" ]]
do
  setup_script=$( find $search_dir -name "setup.sh" )
  search_dir=$(dirname $search_dir)
done
if [[ ${setup_script} == "" ]]; then
  echo "ERROR: setup.sh not found"
  exit 1
fi
source $setup_script

if [[ -z "$COMP_PATH_OTA" ]] ;then
    echo "COMP_PATH_OTA is not set in setup.sh"
    exit 1
fi

if [[ -z "$COMP_PATH_Tools" ]] ;then
    echo "COMP_PATH_Tools is not set in setup.sh"
    exit 1
fi

cd ${cur_dir}
rm -rf Locals/Code/libs
rm -rf Locals/Code/gen
rm -rf Locals/Code/assets
rm -rf Locals/Code/obj
rm -rf Out/Bin/$MODE
rm -rf Out/Doc
mkdir -p Locals/Code/libs

cd ${cur_dir}/../Code

if [ "$MODE" == "Release" ]; then
  echo -e "Mode\t\t: Release"
  OPTIM=release
else
  echo -e "Mode\t\t: Debug"
  OPTIM=debug
fi

BUILD_TAG_FILE=src/com/rosiewallet/BuildTag.java
PACKAGE="com.rosiewallet"
BUILD_TAG=$(grep ComponentBuilder $BUILD_TAG_FILE)
if [ $? -eq 0 ] ; then
    VERSION_NAME=$(echo $BUILD_TAG | cut -d "-" -f1 | tr -d "*[:alpha:][:punct:] ")
    VERSION_NAME="ComponentBuilder $VERSION_NAME"
    VERSION_CODE=$(echo $BUILD_TAG | cut -d "-" -f2 | tr -d "*[:alpha:][:punct:] ")
else
    BUILD_TAG=$(grep BUILD_TAG $BUILD_TAG_FILE)
    VERSION_NAME=$(echo $BUILD_TAG | cut -d "," -f1 | tr -s " " "\n" | \
    grep \@)
    VERSION_CODE=$(echo $BUILD_TAG | cut -d "," -f2 | tr -d "*[:alpha:][:punct:] ")
fi

mkdir assets
mkdir -p libs/armeabi
ant $OPTIM
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed."
    exit 1
fi

mkdir -p $cur_dir/../../Out/Bin/${MODE}
cp -f $cur_dir/../Code/bin/*.apk $cur_dir/../../Out/Bin/$MODE/
