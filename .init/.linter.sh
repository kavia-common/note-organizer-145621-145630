#!/bin/bash
cd /home/kavia/workspace/code-generation/note-organizer-145621-145630/android_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

