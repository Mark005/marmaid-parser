#!/bin/bash

FULL_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

git checkout -b FULL_VERSION
git commit -m "release v ${FULL_VERSION}"
git tag "v${FULL_VERSION}" HEAD
git push origin --all