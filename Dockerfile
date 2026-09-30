# Stage 1: Build Java Servlets, DAOs, and Web Application
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build
COPY . .

# Compile all Java sources including DAOs and Servlets
RUN javac -cp ".:lib/*" *.java && \
    mkdir -p webapp/WEB-INF/classes webapp/WEB-INF/lib && \
    cp *.class webapp/WEB-INF/classes/ && \
    cp lib/* webapp/WEB-INF/lib/ && \
    jar -cvf ROOT.war -C webapp .

# Stage 2: Production Container with Official Apache Tomcat 9
FROM tomcat:9.0-jdk17-temurin-jammy

# Remove default Tomcat webapps (ROOT, docs, examples)
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy ROOT.war so app is accessible directly at root URL (https://your-domain.onrender.com/)
COPY --from=builder /build/ROOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
