To generate the WSDL files, Java 8 requires that you allow downloading schemas attached to the STEP WSDL file.

Put a jaxp.properties file in your /path/to/jdk1.8.0/jre/lib-folder, and insert the follwing line:

javax.xml.accessExternalSchema = all

After this, to run in IDEA:

* Right-click project
* Select Webservices -> Generate Java Code...
* Set WSDL URL (point to a test-system WSDL, for instance. The URL is http://testsystem/StepWS/stepws?wsdl) and package prefix (this code is built with com.stibosystems.soapclientclasses)
* Set username / password in Main.java
* Run Main.java
