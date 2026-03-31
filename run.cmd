@echo off
javac -cp ".;lib/sqlite-jdbc-3.51.3.0.jar:lib/jbcrypt-0.4.jar" .\*.java
java --enable-native-access=ALL-UNNAMED -cp ".;lib/sqlite-jdbc-3.51.3.0.jar:lib/jbcrypt-0.4.jar" Driver