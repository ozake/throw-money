FROM openjdk:8-jdk
ENV APP_HOME=/throw-money
WORKDIR $APP_HOME
# COPY build.gradle settings.gradle gradlew $APP_HOME
# COPY gradle $APP_HOME/gradle
# RUN ./gradlew build || return 0
# COPY . .
# RUN ./gradlew build
#
# FROM openjdk:8-jdk
#
# VOLUME /tmp
# ARG JAR_FILE=build/libs/throw-money-0.0.1-SNAPSHOT.jar
# COPY ${JAR_FILE} throw-money.jar
# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/throw-money.jar"]