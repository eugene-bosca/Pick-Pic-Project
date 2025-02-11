#!/bin/bash

PREV=$(pwd)
GIT_ROOT=$(git rev-parse --show-toplevel)
FRONTEND=$GIT_ROOT/frontend

echo "Running frontend checks."
cd $FRONTEND

echo "Pre-commit hook: running ./gradlew check"
./gradlew check

if [ $? -ne 0 ]; then
    echo "Checks failed, commit aborted."
    exit 1
fi

echo "Pre-commit hook: running ./gradlew lint"
./gradlew lint

if [ $? -ne 0 ]; then
    echo "Linting failed, commit aborted."
    exit 1
fi

cd $PREV

echo "Pre-commit hook passed, proceeding with commit."
exit 0

