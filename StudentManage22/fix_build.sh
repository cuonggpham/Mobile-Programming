#!/bin/bash
cd /home/dell/Documents/Code/Android/StudentManage22
echo "Cleaning build directories..."
rm -rf app/build
rm -rf build
rm -rf .gradle
echo "Running Gradle clean..."
./gradlew clean
echo "Rebuilding project..."
./gradlew build
echo "Done!"

