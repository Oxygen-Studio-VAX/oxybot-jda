cp -f .env ./builds/libs
rm -rf ./builds/libs/logs
java -jar ./builds/libs/bot-1.0.0-SNAPSHOT.2.jar