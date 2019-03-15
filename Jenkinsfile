pipeline {
  agent none
  environment {
    SERVICE_NAME='peregrinus'
    DOCKER_IMAGE='bremersee/peregrinus'
    DEV_TAG='latest'
    PROD_TAG='release'
  }
  stages {
    stage('Build') {
      agent {
        label 'maven'
      }
      steps {
        sh 'mvn clean compile'
      }
    }
    stage('Test') {
      agent {
        label 'maven'
      }
      steps {
        sh 'mvn test'
      }
    }
    stage('Push') {
      agent {
        label 'maven'
      }
      when {
        anyOf {
          branch 'develop'
          branch 'master'
        }
      }
      steps {
        sh 'mvn -DskipTests -Ddockerfile.skip=false package dockerfile:push'
      }
    }
    stage('Push latest') {
      agent {
        label 'maven'
      }
      when {
        branch 'develop'
      }
      steps {
        sh 'mvn -DskipTests -Ddockerfile.skip=false -Ddockerfile.tag=latest package dockerfile:push'
      }
    }
    stage('Site') {
      agent {
        label 'maven'
      }
      when {
        anyOf {
          branch 'develop'
          branch 'master'
        }
      }
      steps {
        sh 'mvn site-deploy'
      }
    }
    stage('Deploy on dev-swarm') {
      agent {
        label 'dev-swarm'
      }
      when {
        branch 'develop'
      }
      steps {
        sh '''
          if docker service ls | grep -q ${SERVICE_NAME}; then
            echo "Updating service ${SERVICE_NAME} with docker image ${DOCKER_IMAGE}:${DEV_TAG}."
            docker service update --image ${DOCKER_IMAGE}:${DEV_TAG} ${SERVICE_NAME}
          else
            echo "Creating service ${SERVICE_NAME} with docker image ${DOCKER_IMAGE}:${DEV_TAG}."
            chmod 755 docker-swarm/service.sh
            docker-swarm/service.sh "${DOCKER_IMAGE}:${DEV_TAG}"
          fi
        '''
      }
    }
  }
}