
            == Grid Access Triage Engine (GATE) ==

1. Build prerequisites

  a. ~/.m2/settings.xml

     Here is an incomplete version that I currently use.

<pre><![CDATA[
<settings>
  <profiles>
    <profile>
      <id>dev</id>
      <repositories>
        <repository>
          <id>renci.repository</id>
	  <name>renci.repository</name>
	  <url>http://archiva.renci.org:8080/repository/internal</url>
	  <releases>
	    <enabled>true</enabled>
	  </releases>
	  <snapshots>
	    <enabled>false</enabled>
	  </snapshots>
        </repository>
        <repository>
	  <id>renci.snapshot.repository</id>
	  <name>renci.repository</name>
	  <url>http://archiva.renci.org:8080/repository/snapshots</url>
	  <releases>
	    <enabled>false</enabled>
	  </releases>
	  <snapshots>
	    <enabled>true</enabled>
	  </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>dev</activeProfile>
  </activeProfiles>
</settings>
]]></pre>
  
  b. JAVA_HOME must be set

  c. Maven 3 must be installed and M2_HOME must be set 

2. How to build

    Run 'mvn clean install'




