pipeline {
    agent any
    stages {
        stage('Clone') {
            steps {
                git branch: "prod", url: 'http://192.168.1.21/root/nbd-api.git'
            }
        }
        stage('Build') {

            steps {
                script {
                     def pom = readMavenPom file: 'pom.xml'
                     version = pom.version
                     name = pom.name
                    sh 'mvn install:install-file -Dfile=/app/ojdbc8-12.2.0.1.jar -DgroupId=com.oracle.jdbc -DartifactId=ojdbc8 -Dversion=12.2.0.1 -Dpackaging=jar -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -Dspring.profiles.active=prod'
                    sh 'mvn -DskipTests clean verify install'
                    sh 'mvn dockerfile:build'
                    sh "docker-compose up -d --build"
                }
            }
        }
    }
}
