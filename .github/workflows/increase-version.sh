#!/bin/bash

FULL_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo $FULL_VERSION

MAJOR_VERSION=$(cut -d "." -f1 <<< "$sss")
MINOR_VERSION=$(cut -d "." -f2 <<< "$sss")
PATCH_VERSION=$(cut -d "." -f3 <<< "$sss")

echo $((++MAJOR_VERSION))
echo $((++MINOR_VERSION))
echo $((++PATCH_VERSION))