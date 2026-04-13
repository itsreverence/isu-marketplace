#!/usr/bin/env bash
javac -cp ".:lib/sqlite-jdbc-3.51.3.0.jar:lib/bcrypt-0.10.2.jar:lib/bytes-1.6.1.jar:lib/json-simple-1.1.1.jar" ./*.java
java --enable-native-access=ALL-UNNAMED -cp ".:lib/sqlite-jdbc-3.51.3.0.jar:lib/bytes-1.6.1.jar:lib/bcrypt-0.10.2.jar:lib/json-simple-1.1.1.jar" Driver