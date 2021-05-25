#!/bin/bash
git pull origin dev

if [ -n "$1" ]; then
  ./gradlew run --args="$*"
else
  ./gradlew run
fi
