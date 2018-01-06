/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qait.automation.TestInitiator;

import com.qait.automation.utils.PropFileHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Reporter;

public class WebDriverFactory {

	private String browser = "";

	public WebDriverFactory() {

	}

	public WebDriverFactory(String browserName) {
		browser = browserName;
	}

	private static final DesiredCapabilities capabilities = new DesiredCapabilities();

	public WebDriver getDriver(Map<String, String> seleniumconfig) {



		browser = System.getProperty("browser");
		if (browser == null || browser.isEmpty()) {
			browser = seleniumconfig.get("browser");
		}
		Reporter.log("[INFO]: The test Browser is " + browser.toUpperCase() + " !!!", true);
		String server = System.getProperty("server");
		if (server == null) {
			server = seleniumconfig.get("seleniumserver");
		}
		if (server.equalsIgnoreCase("local")) {
			if (browser.equalsIgnoreCase("firefox")) {
				return getFirefoxDriver();
			} else if (browser.equalsIgnoreCase("chrome")) {
				return getChromeDriver(seleniumconfig.get("driverpath"));
			} else if (browser.equalsIgnoreCase("Safari")) {
				return getSafariDriver();
			} else if ((browser.equalsIgnoreCase("ie")) || (browser.equalsIgnoreCase("internetexplorer"))
					|| (browser.equalsIgnoreCase("internet explorer"))) {
				return getInternetExplorerDriver(seleniumconfig.get("driverpath"));
			} // TODO: treat mobile browser and separate instance on lines of
			// remote driver
			else if (browser.equalsIgnoreCase("mobile")) {
				return setMobileDriver(seleniumconfig);
			}
		}
		if (server.equalsIgnoreCase("remote")) {
			return setRemoteDriver(seleniumconfig);
		}

		if(server.equalsIgnoreCase("browserstack")){
			System.out.println("Hello: "+server);
			return setBrowserStackDriver(seleniumconfig);
		}


		return new FirefoxDriver();
	}

	private WebDriver setRemoteDriver(Map<String, String> selConfig) {
		DesiredCapabilities cap = null;
		if (browser.equalsIgnoreCase("firefox")) {
			cap = DesiredCapabilities.firefox();
		} else if (browser.equalsIgnoreCase("chrome")) {
			cap = DesiredCapabilities.chrome();
		} else if (browser.equalsIgnoreCase("Safari")) {
			cap = DesiredCapabilities.safari();
		} else if ((browser.equalsIgnoreCase("ie")) || (browser.equalsIgnoreCase("internetexplorer"))
				|| (browser.equalsIgnoreCase("internet explorer"))) {
			cap = DesiredCapabilities.internetExplorer();
		}
		String seleniuhubaddress = System.getProperty("vm.IP");
		if (seleniuhubaddress == null || seleniuhubaddress.isEmpty()) {
			seleniuhubaddress = selConfig.get("seleniumserverhost");
		}

		URL selserverhost = null;
		try {
			selserverhost = new URL(seleniuhubaddress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// TODO handle dereferencing by using capabilities set browser
		cap.setJavascriptEnabled(true);
		return new RemoteWebDriver(selserverhost, cap);
	}


	private WebDriver setBrowserStackDriver(Map<String, String> selConfig){
		DesiredCapabilities caps = null;
		if (browser.equalsIgnoreCase("firefox")) {
			caps = DesiredCapabilities.firefox();
		} else if (browser.equalsIgnoreCase("chrome")) {
			caps = DesiredCapabilities.chrome();
		} else if (browser.equalsIgnoreCase("Safari")) {
			caps = DesiredCapabilities.safari();
		} else if ((browser.equalsIgnoreCase("ie")) || (browser.equalsIgnoreCase("internetexplorer"))
				|| (browser.equalsIgnoreCase("internet explorer"))) {
			caps = DesiredCapabilities.internetExplorer();
		}
		String browser=System.getProperty("browser");
		String browserVersion=System.getProperty("browserVersion");
		String os=System.getProperty("os");
		String os_version=System.getProperty("osVersion");
		String resolution=System.getProperty("resolution");
		if(browser== null || browser.isEmpty()){
			caps.setCapability("browser", selConfig.get("browser"));}
		else{
			caps.setCapability("browser", System.getProperty("browser"));
		}
		if(browserVersion== null || browserVersion.isEmpty()){
			caps.setCapability("browser_version", selConfig.get("browserVersion"));
			System.out.println("Version "+selConfig.get("browserVersion"));}
		else{
			caps.setCapability("browser_version", System.getProperty("browserVersion"));
		}
		
		if(os== null || os.isEmpty()){
			caps.setCapability("os", selConfig.get("os"));
		}else{
			caps.setCapability("os", System.getProperty("os"));
		}
		if(os_version==null || os_version.isEmpty()){
			caps.setCapability("os_version", selConfig.get("os_version"));}
		else{
			caps.setCapability("os_version", System.getProperty("osVersion"));
		}
		if(resolution==null || resolution.isEmpty()){
			caps.setCapability("resolution", selConfig.get("resolution"));}
		else{
			caps.setCapability("resolution", System.getProperty("resolution"));
		}
		String seleniuhubaddress = System.getProperty("vm.IP");
		if (seleniuhubaddress == null || seleniuhubaddress.isEmpty()) {
			seleniuhubaddress = selConfig.get("browserStackserverhost");
		}

		URL selserverhost = null;
		try {
			selserverhost = new URL(seleniuhubaddress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// TODO handle dereferencing by using capabilities set browser
		caps.setJavascriptEnabled(true);
		return new RemoteWebDriver(selserverhost, caps);
	}

	private static WebDriver getChromeDriver(String driverpath) {
		//        String agentName = PropFileHandler.readProperty("userAgent");
		//        int index1 = agentName.indexOf("(");
		//        int index2 = agentName.indexOf(")");
		//        agentName = agentName.substring(index1 + 1, index2);
		String localMachineEnvironment = System.getProperty("os.name");
		if (localMachineEnvironment.toLowerCase().trim().contains("mac")) {
			System.setProperty("webdriver.chrome.driver", driverpath + "chromedriver");
		} else if (driverpath.endsWith(".exe") || driverpath.endsWith("chromedriver")) {
			System.setProperty("webdriver.chrome.driver", driverpath);
		} else {
			System.setProperty("webdriver.chrome.driver", driverpath + "chromedriver.exe");
		}

		//        } else if (agentName.toLowerCase().trim().contains("macintosh")) {
		//            System.setProperty("webdriver.chrome.driver", driverpath + "chromedriverMac");
		//        }
		ChromeOptions options = new ChromeOptions();
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		cap.setCapability(ChromeOptions.CAPABILITY, options);
		return new ChromeDriver(cap);
	}

	private static WebDriver getInternetExplorerDriver(String driverpath) {
		if (driverpath.endsWith(".exe")) {
			System.setProperty("webdriver.ie.driver", driverpath);
		} else {
			System.setProperty("webdriver.ie.driver", driverpath + "IEDriverServer.exe");
		}
		capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		capabilities.setCapability("ignoreZoomSetting", true);
		return new InternetExplorerDriver(capabilities);
	}

	private static WebDriver getSafariDriver() {
		return new SafariDriver();
	}

	private static WebDriver getFirefoxDriver() {
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.cache.disk.enable", false);
		return new FirefoxDriver(profile);
	}

	private WebDriver setMobileDriver(Map<String, String> selConfig) {
		DesiredCapabilities cap = new DesiredCapabilities();
		String[] appiumDeviceConfig = selConfig.get("mobileDevice").split(":");

		cap.setCapability("deviceName", appiumDeviceConfig[0]);
		cap.setCapability("device", appiumDeviceConfig[1]);
		cap.setCapability("platformName", appiumDeviceConfig[1]);
		cap.setCapability("app", appiumDeviceConfig[2]);
		cap.setCapability(CapabilityType.VERSION, "5.0.2");
		cap.setCapability(CapabilityType.PLATFORM, "Windows");
		String appiumServerHostUrl = selConfig.get("appiumServer");
		URL appiumServerHost = null;
		try {
			appiumServerHost = new URL(appiumServerHostUrl);
		} catch (MalformedURLException e) {

		}
		cap.setJavascriptEnabled(true);
		System.out.println(appiumServerHostUrl);
		return new RemoteWebDriver(appiumServerHost, cap);
	}

}
