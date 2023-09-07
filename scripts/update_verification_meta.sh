#!/usr/bin/env bash
cd ..
./gradlew --write-verification-metadata pgp,sha512 help
