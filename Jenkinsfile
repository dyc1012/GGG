pipeline {
	parameters {
		string(name: 'agent', defaultValue: 'slave001', description: 'which agent/node do you want to run?')
	}
	
    agent {
        label "${params.agent}"
    }
	
	environment {
		DISABLE_AUTH = 'true'
		DB_ENGINE    = 'sqlite'
	}
	
    stages {
        stage('Build') {            
            steps {                
                echo 'Building.............dd'            
            }        
        }        
        stage('Test') {            
            steps {  
				retry(3) {
					echo 'Testing................'
				}
                          
            }        
        }
        stage('Deploy - Staging') {            
            steps {                
				timeout(time: 3, unit: 'MINUTES') {
					echo './deploy staging'
					echo './run-smoke-tests'
				}
                           
            }        
        }        
        stage('Sanity check') {            
            steps {                
                input "Does the staging environment look ok?"            
            }        
        }        
        stage('Deploy - Production') {            
            steps {                
                echo './deploy production'            
            }        
        }    
    }
 
    post {        
        always {            
            echo 'One way or another, I have finished'            
            deleteDir() /* clean up our workspace */        
        }        
        success {            
            echo 'I succeeeded!'        
        }        
        unstable {            
            echo 'I am unstable :/'        
        }        
        failure {            
            echo 'I failed :('        
        }        
        changed {            
            echo 'Things were different before.............'        
        }    
    }
}
