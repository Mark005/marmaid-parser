#!/bin/bash

FULL_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "full_version=${FULL_VERSION}" >> $GITHUB_OUTPUT

git config --global user.email "git.action@github.com"
git config --global user.name "CI Action"
git checkout -b "release/${FULL_VERSION}"
git commit -m "release v${FULL_VERSION}"
git tag -a -m "Releasing version ${FULL_VERSION}" "v${FULL_VERSION}"

git push origin --all