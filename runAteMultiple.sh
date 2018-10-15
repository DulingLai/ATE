#! /bin/bash

if [ $# -ne 1 ]; then 
    echo "Please enter an APK directory "
    exit
fi

for file in ${1}/*.apk
do
    echo "Working on: ${file}"
    java -jar ate.jar ate -i ${file}
done