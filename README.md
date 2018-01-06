MindTap selenium-automation
===================

### MT/MTX Automation [Java - Selenium Webdriver - Maven - TestNG]
---
#### Summary:

This Test Automation Framework serves the purpose of automating the test cases and getting the tests results. Please refer the below mentioned guidelines/prerequisites for the same.

#### System Requirement:

* JDK 1.8 or above
* Maven 3.1 or above
* Eclipse or IDE of choice in case there is need to update the script. (optional)
* Netbeans : Preferred for additional features like auto-code indentation, dependency error detection & many more.

#### Test Execution:

All the test suites can be configured and kept in **./src/test/resources/testxml** folder

##### Executing the default tests as mentioned in the TestNG.xml

    mvn clean integration-test -Dserver=remote/local(implicit) -Dbrowser=chrome(implicit)/firefox -Dtier=qad(implicit)/stg -Dtest=<testClassName>
	
**Note**: 
1) if -Dserver is set to remote then an additional parameter -Dvm.IP="target machine Address" is also required which contains the target host IP address where all the testscripts will get executed.
2) A selenium server instance must be running at the target machine for interpreting selenese commands.
3) Currently supported selenium standalone server version:2.48.2  ; FF version :40.0 ; Chrome version: 45.0;*

##### Executing custom suite of tests (e.g. as defined in NewTestNG.xml)

    mvn clean integration-test -Dtestxml=<NewTestNGfileName>.xml

##### Executing a single test (e.g. Admin_Login_Layout_Test)

    mvn clean integration-test -Dtest=Admin_Login_Layout_Test

#### Guidelines for code review:
* Use camelCase coding conventions for writing test scripts literals (method names , keywords , variable Names & locators in spec files)
* Spec files contains all the locator values for webElements of certain pages . If test cases fails due to NoSuchElementException , change the locator values.
* Plugins to be managed via POM only. Please don't add any external jars/exe in project.
* Assign specific names for the variables
* Remove dead/obsolete code/warnings if any
* Don't use system.out.print() to print the logs. use Reporter class methods
* Write JavaDoc for each method

#### Result Files:	
The Test Execution Results will be stored in the following directory once the test has completed

##### TestNG Surefire reports
    ./target/surefire-reports/emailable-report.html (for single test suite)
	
##### Galen Layout reports
    ./target/galen-reports/report.html
##### Failure screenshots: (Screenshots are captured only if the test had failed due to some known reason).
	 ./target/screenshots
-------------
-------------
# MT_Automation_V8
MT Automation using V8 frame work