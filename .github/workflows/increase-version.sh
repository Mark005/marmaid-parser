#!/bin/bash

FULL_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo $FULL_VERSION

MAJOR_VERSION=$(cut -d "." -f1 <<< "$FULL_VERSION")
MINOR_VERSION=$(cut -d "." -f2 <<< "$FULL_VERSION")
PATCH_VERSION=$(cut -d "." -f3 <<< "$FULL_VERSION")

echo $((++MAJOR_VERSION))
echo $((++MINOR_VERSION))
echo $((++PATCH_VERSION))