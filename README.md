
            == Grid Access Triage Engine (GATE) ==

1. Build prerequisites

  a. ~/.m2/settings.xml

     Here is an incomplete version that I currently use.
  
  <profiles>
    <profile>
      <id>dev</id>
      <properties>
      </properties>
      <repositories>
	<repository>
	  <id>renci.repository</id>
	  <name>renci.repository</name>
	  <url>http://beluga.renci.org/m2/repository</url>
	</repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>dev</activeProfile>
  </activeProfiles>

  b. JAVA_HOME must be set

     For example, 'export JAVA_HOME=~/jdk-1.6.0_02'

  c. maven2 must be installed and M2_HOME must be set 

     For example, 'export M2_HOME=~/m2'

How to build

    Run 'mvn clean install'




