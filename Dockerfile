# Stage 1: Build Java Servlets, DAOs, and Web Application
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build
COPY . .

# Compile all Java source packages into webapp/WEB-INF/classes
RUN mkdir -p webapp/WEB-INF/classes webapp/WEB-INF/lib && \
    javac -cp ".:lib/*" -d webapp/WEB-INF/classes \
        src/com/helpdesk/util/*.java \
        src/com/helpdesk/model/*.java \
        src/com/helpdesk/dao/*.java \
        src/com/helpdesk/servlet/*.java \
        src/com/helpdesk/*.java && \
    cp lib/* webapp/WEB-INF/lib/ && \
    jar -cvf ROOT.war -C webapp .

# Stage 2: Production Container with Official Apache Tomcat 9
FROM tomcat:9.0-jdk17-temurin-jammy

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy ROOT.war so app is accessible directly at root URL (https://your-domain.onrender.com/)
COPY --from=builder /build/ROOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
