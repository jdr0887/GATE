
            == Grid Access Triage Engine (GATE) ==

1. Build prerequisites

  a. ~/.m2/settings.xml

     Here is an incomplete version that I currently use.

<pre>
&lt;settings&gt;
  &lt;profiles&gt;
    &lt;profile&gt;
      &lt;id&gt;dev&lt;/id&gt;
      &lt;repositories&gt;
        &lt;repository&gt;
          &lt;id&gt;renci.repository&lt;/id&gt;
          &lt;name&gt;renci.repository&lt;/name&gt;
          &lt;url&gt;http://archiva.renci.org:8080/repository/internal&lt;/url&gt;
          &lt;releases&gt;
            &lt;enabled&gt;true&lt;/enabled&gt;
          &lt;/releases&gt;
          &lt;snapshots&gt;
            &lt;enabled&gt;false&lt;/enabled&gt;
          &lt;/snapshots&gt;
        &lt;/repository&gt;
      &lt;/repositories&gt;
    &lt;/profile&gt;
  &lt;/profiles&gt;
  &lt;activeProfiles&gt;
    &lt;activeProfile&gt;dev&lt;/activeProfile&gt;
  &lt;/activeProfiles&gt;
&lt;/settings&gt;
</pre>
  
  b. JAVA_HOME must be set

  c. Maven 3 must be installed and M2_HOME must be set 

2. How to build

    Run 'mvn clean install'
