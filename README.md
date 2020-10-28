```
# you can package and run
mvn package -DskipTests
java -jar target/reactive-messaging.jar

curl -X POST -H "Content-type: application/json" -d '{"key":"key1","value":"val1"}' localhost:8080/reactive-messaging/process

# but you cannot mvn test - due to MsgProcessingBean2

mvn test
# ...stuck
```

