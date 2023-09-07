#!/usr/bin/env bash
cd ..
./gradlew --write-verification-metadata pgp,sha256,sha512 --export-keys
