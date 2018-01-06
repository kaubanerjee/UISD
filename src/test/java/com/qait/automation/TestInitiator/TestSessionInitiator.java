package com.qait.automation.TestInitiator;

import com.cengage.mindtap.keywords.*;
import static com.qait.automation.utils.YamlReader.getData;
import static com.qait.automation.utils.YamlReader.setYamlFilePath;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;
import com.qait.automation.utils.BrowserConsolelog;
import com.qait.automation.utils.HttpClient;
import com.qait.automation.utils.PropFileHandler;
import com.qait.automation.utils.TakeScreenshot;
import com.sun.jersey.api.client.ClientResponse;
import com.thoughtworks.selenium.webdriven.commands.WaitForPageToLoad;

import static com.qait.automation.utils.ConfigPropertyReader.getProperty;

public class TestSessionInitiator {

    public static String browserVersion = "";
    public TakeScreenshot takescreenshot;
    public BrowserConsolelog browserconsolelog;
    public PropFileHandler data;
    protected WebDriver driver;
    String browser;
    String seleniumserver;
    String seleniumserverhost;
    private final WebDriverFactory wdfactory;
    private String testname;
    public TestSessionInitiator test;

    public TestSessionInitiator(String testname, String browserName) {
        wdfactory = new WebDriverFactory(browserName);
        this.testname = testname;
        testInitiator(testname);
    }

    // public TestSessionInitiator(String testname) {
    // this(testname, "");
    // }
    public WebDriver getDriver() {
        return this.driver;
    }

    public void codestartedFor(String s) {
        Reporter.log("--------------------------------------------------", true);
        Reporter.log("--------------------------------------------------", true);
        Reporter.log("Code Started for :-  \"" + s + "\"", true);
        Reporter.log("--------------------------------------------------", true);
        Reporter.log("--------------------------------------------------", true);
    }

    public void launchApplicationAsStudent() {
        launchApplication("student");
    }

    public void launchApplication(String username, String userPassword, String CourseKey, String ISBN) {
        Reporter.log("** Test Data **", true);
        Reporter.log("Login ID:- " + username, true);
        Reporter.log("Login Password:- " + userPassword, true);
        Reporter.log("Course Key :- " + CourseKey, true);
        Reporter.log("Book ISBN:- " + ISBN, true);

        try {
            String ssoToken = getSSOToken(username, userPassword);
            String destinationURL = buildLaunchUrl(ssoToken, CourseKey, ISBN);

            openUrl(destinationURL);
            PropFileHandler.writeProperty("buildURL", destinationURL);
            Reporter.log(username + " successfully logged in to the application", true);
        } catch (JSONException e) {
            Reporter.log("[ERROR]: Could not log in to application, trouble authenticating: " + e.toString(), true);
        }
    }

    public String newCourseKey(String courseKey) {

        return courseKey.replaceAll("-", "");

    }

    public void launchApplication(String base_url) {
        Reporter.log(" The application url is :- " + base_url, true);
        String uAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        Reporter.log("Current OS Browser configuration:" + uAgent, true);
        driver.manage().deleteAllCookies();
        driver.get(base_url);
    }

    public void openUrl(String url) {
        Reporter.log("Aplication URl: " + url, true);
        driver.manage().deleteAllCookies();
        driver.get(url);
    }

    private String buildLaunchUrl(String ssoToken, String CourseKey, String ISBN) {
        StringBuilder sb = new StringBuilder();
        sb.append(getData("PRODUCT_URL"));
        sb.append("?token=");
        sb.append(ssoToken);
        sb.append("&courseKey=");
        sb.append(CourseKey);
        sb.append("&eISBN=");
        sb.append(ISBN);
        return sb.toString();
    }

    public void launchUrl(String url) {
        Reporter.log("[INFO]: The Generic URL :- " + url, true);
        driver.get(url);
    }

    public void closeTestSession() {
        System.out.println("\n");
        Reporter.log("[INFO]: The Test: \"" + this.testname.toUpperCase() + "\" COMPLETED!" + "\n", true);
        if (driver != null) {
            driver.quit();
        }
    }

    @SuppressWarnings("static-access")
    public void clear_Prop() {
        data.clearProperty();
    }

    private void _initPage() {
        data = new PropFileHandler();
        browserconsolelog = new BrowserConsolelog(testname, driver);
        takescreenshot = new TakeScreenshot(testname, this.driver);
        browserconsolelog = new BrowserConsolelog(testname, this.driver);
    }

    private void testInitiator(String testname) {
        setYamlFilePath();
        _configureBrowser();
        _initPage();
        codestartedFor(testname);
        // takescreenshot = new TakeScreenshot(testname, this.driver);
        // browserconsolelog = new BrowserConsolelog(testname, this.driver);
    }

    @SuppressWarnings("static-access")
    private void _configureBrowser() {
        driver = wdfactory.getDriver(_getSessionConfig());
        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        browserVersion = caps.getVersion();
        String uAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        data.writeProperty("userAgent", uAgent);
        data.writeProperty("browserVersion", browserVersion);
        System.out.println("Browser Version :::: " + browserVersion);
        if (!_getSessionConfig().get("browser").toLowerCase().trim().equalsIgnoreCase("mobile")) {
            driver.manage().window().maximize();
        }
        int timeOuts = Integer.parseInt(getProperty("./Config.properties", "timeout"));
        driver.manage().timeouts().implicitlyWait(timeOuts, TimeUnit.SECONDS);
    }

    private Map<String, String> _getSessionConfig() {
        String[] configKeys = {"tier", "browser", "seleniumserver", "seleniumserverhost", "timeout", "driverpath",
            "appiumServer", "mobileDevice", "browserStackserverhost", "browserVersion", "os", "osVersion", "resolution"};
        Map<String, String> config;
        config = new HashMap<>();
        for (String string : configKeys) {
            config.put(string, getProperty("./Config.properties", string));
        }
        return config;
    }

    // private String getSSOToken(String username, String password) throws
    // JSONException {
    // Object postBody = ("{\"uid\":\"" + username + "\", \"password\":\"" +
    // password + "\"}");
    // HttpClient httpclient = new HttpClient();
    // ClientResponse response =
    // httpclient.postHttpResponse(getData("SSO_TOKEN_URL"), postBody);
    // String entity = response.getEntity(String.class);
    // return new JSONObject(entity).getString("token");
    // }
    private String getSSOToken(String username, String password) throws JSONException {
        Object postBody = ("{\"uid\":\"" + username + "\", \"password\":\"" + password + "\"}");
        HttpClient httpclient = new HttpClient();
        ClientResponse response = httpclient.postHttpResponse(getData("SSO_TOKEN_URL"), postBody);
        String entity = response.getEntity(String.class);
        String token = new JSONObject(entity).getString("token");
        if (token != "null") {

        } else {
            response = httpclient.postHttpResponse(getData("sso_token_url_new"), postBody);
            entity = response.getEntity(String.class);
            token = new JSONObject(entity).getString("token");
        }
        return token;
    }

    private String buildLaunchUrl(String ssoToken) {
        StringBuilder sb = new StringBuilder();
        sb.append(getData("PRODUCT_URL"));
        sb.append("/?token=");
        sb.append(ssoToken);
        sb.append("&courseKey=");
        sb.append(getData("COURSE_KEY"));
        sb.append("&eISBN=");
        sb.append(getData("SSO_ISBN"));
        return sb.toString();
    }

    public void stepStartMessage(String testStepName) {
        Reporter.log(" ", true);
        Reporter.log("------------------------------------------------------------------------------", true);
        Reporter.log("***** STARTING TEST STEP:- " + testStepName + " *****", true);
        Reporter.log(" ", true);
    }
}
