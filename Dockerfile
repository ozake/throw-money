FROM openjdk:8-jdk

COPY . /throw-money
WORKDIR /throw-money

CMD ["./gradlew", "bootRun"]