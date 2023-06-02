#!/bin/bash

# exit immediately if a command fails
set -e

java -version

echo
echo "======================================================="
echo "Building..."
echo "======================================================="
echo
./mvnw --show-version --batch-mode clean verify

echo
echo "======================================================="
echo "Running PMD..."
echo "======================================================="
echo
cd pmd-java-dist/target
unzip -q pmd-java-bin-1.0.0-SNAPSHOT.zip
pmd-java-bin-1.0.0-SNAPSHOT/bin/run.sh pmd --no-cache \
    -f text \
    -d ../../ \
    -R custom-java-ruleset.xml \
    --fail-on-violation false \
    --report-file pmdreport.txt

grep "examples/java/rules/MyRule.java" pmdreport.txt || (echo -e "\n\n\x1b[31mMissing expected rule violation\e[0m"; exit 1)

echo -e "\n\n\x1b[32mTest successful\e[0m"
