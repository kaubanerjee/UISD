package com.cengage.mindtap.stepdefs;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import com.qait.automation.TestInitiator.TestSessionInitiator;
import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(features = "src/test/resources/featureFiles/", tags = {""}, format = {"json:target/cucumber/report.json" })

public class SingleFeature_Test extends AbstractTestNGCucumberTests {

	public static TestSessionInitiator test;

	@BeforeClass
	public void setup() {
		test = new TestSessionInitiator(this.getClass().getSimpleName(), "");
	}

	@AfterClass
	public void closeTestSession() {
		test.closeTestSession();
	}
}	