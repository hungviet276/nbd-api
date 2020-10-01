pipeline {
    agent any
    stages {
        stage('Clone') {
            steps {
                git branch: "master", url: 'https://10.252.10.175/thanglv/nbd-api.git'
            }
        }
        stage('Build') {

            steps {
                script {
                     def pom = readMavenPom file: 'pom.xml'
                     version = pom.version
                     name = pom.name
                    sh 'mvn clean package dockerfile:build'
                    sh "docker push levietthang1997/${name}:${version}"
                    sh "docker-compose up -d --build"
                }
            }
        }
    }
}
