
we have to add 

		<dependency>
			<groupId>com.webhook</groupId>
			<artifactId>aligenie-semantic</artifactId>
			<version>1.0</version>
		</dependency>
		
with mvn command like

mvn install:install-file -Dfile=${project.basedir}\src\main\resources\bin\semantic-execute-meta-1.0.4-SNAPSHOT.jar -DgroupId=com.webhook -DartifactId=aligenie-semantic -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=${project.basedir}\src\main\resources\bin\IKAnalyzer2012_u6.jar -DgroupId=com.ikanalyzer -DartifactId=ikanalyzer -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=${project.basedir}\src\main\resources\bin\lucene-core-3.6.0.jar -DgroupId=com.ikanalyzer.lucence -DartifactId=ikanalyzerlucence -Dversion=1.0 -Dpackaging=jar

${project.basedir} -> your project path