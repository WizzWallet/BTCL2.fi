ARG base_image=azul/zulu-openjdk:8
FROM ${base_image}
ENV SRPING_PROFILES_ACTIVE=dev
RUN mkdir /app
WORKDIR /app

COPY  ./server/target/fi-1.0-SNAPSHOT.jar /app/service.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Ddruid.mysql.usePingMethod=false","-Djava.security.egd=file:/dev/./urandom","-jar", "/app/service.jar"]
