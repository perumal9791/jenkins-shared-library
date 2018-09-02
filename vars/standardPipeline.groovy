def call(body) {

	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
	body.delegate = config
	body()

	node {
		// Clean workspace before doing anything
		deleteDir()

		try {
			stage ('Checkout SCM') {   
				checkout scm
			}		
			stage ('Compile Stage') {   
				sh "echo 'building ${config.projectName} ...'"
				withMaven(maven : 'maven2018') {
					bat 'mvn clean compile'
				}
			}
			stage ('Testing Stage') {
				withMaven(maven : 'maven2018') {
					bat 'mvn test'
				}
			}
			stage ('Deployment Stage') {
				withMaven(maven : 'maven2018') {
					bat 'mvn deploy'
				}
			}			       
		} catch (err) {
			currentBuild.result = 'FAILED'
			throw err
		}
	}
}
