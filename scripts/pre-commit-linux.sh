#!/bin/bash

echo "Precommit hook: running ./gradlew check"
./gradlew check

if [ $? -ne 0 ]; then
    echo "Checks failed, commit aborted."
    exit 1
fi

echo "Precommit hook: running ./gradlew lint"
./gradlew lint

if [ $? -ne 0 ]; then
    echo "Linting failed, commit aborted."
    exit 1
fi

echo "Precommit hook passed, proceeding with commit."
exit 0

