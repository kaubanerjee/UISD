/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qait.automation.getpageobjects;

import static com.qait.automation.getpageobjects.ObjectFileReader.getPageTitleFromFile;
import static com.qait.automation.utils.ConfigPropertyReader.getProperty;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.qait.automation.utils.Color_Encoding;
import static com.qait.automation.utils.ConfigPropertyReader.getProperty;
import com.qait.automation.utils.PropFileHandler;
import com.qait.automation.utils.ReportMsg;
import com.qait.automation.utils.SeleniumWait;
import com.qait.automation.utils.TakeScreenshot;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

/**
 * @author QAIT
 *
 */
public class BaseUi {

    protected WebDriver driver;
    protected SeleniumWait wait;
    private String pageName;
    public Color_Encoding colorEncoding;
    protected final int AJAX_WAIT = 5;
    protected PropFileHandler data;
    public TakeScreenshot tks;

    protected BaseUi(WebDriver driver, String pageName) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
        this.pageName = pageName;
        this.wait = new SeleniumWait(driver, Integer.parseInt(getProperty("Config.properties", "timeout")));
        colorEncoding = new Color_Encoding();
        tks=new TakeScreenshot(driver);
        
    }

    protected String getPageTitle() {
        return driver.getTitle();
    }

    protected String logMessage(String message) {
        Reporter.log(message, true);
        return message;
    }

    protected String getCurrentURL() {
        return driver.getCurrentUrl();
    }
    
    protected List<WebElement> elementsByDynamicXpath(String path){
		return driver.findElements(By.xpath(path));
	}
    
    protected WebElement elementByDynamicXpath(String path) {
		return driver.findElement(By.xpath(path));
	}

    protected void verifyPageUrlContains(String expectedPageUrl) {

        wait.waitForPageToLoadCompletely();
        String currenturl = getCurrentURL();
        Assert.assertTrue(currenturl.toLowerCase().trim().contains(expectedPageUrl.toLowerCase()),
                logMessage("[INFO]: verifying: URL - " + currenturl + " of the page '" + pageName + "' contains: "
                        + expectedPageUrl));
        logMessage("[ASSERT PASSED]: URL of the page " + pageName + " contains:- " + expectedPageUrl);

    }

    protected void switchToFrame(WebElement element) {
        wait.waitForElementToBeVisible(element);
        driver.switchTo().frame(element);
    }

    protected void fireOnClickJsEvent(String elementRef, String index) {
        ((JavascriptExecutor) driver).executeScript(""
                + "var elem = document.getElementsByClassName('" + elementRef + "')[" + index + "];"
                + "if( document.createEvent ) { "
                + "   var evObj = document.createEvent('MouseEvents');"
                + "    evObj.initEvent( 'click', true, false );"
                + "   elem.dispatchEvent(evObj);"
                + "} else if( document.createEventObject ) {"
                + "    elem.fireEvent('onclick');"
                + "}"
                + "");
    }
    
    public void switchToFrame(int i) {
        driver.switchTo().frame(i);
    }

    public void switchToFrame(String id) {
        driver.switchTo().frame(id);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }
    protected void executeJavascript(String script) {
        ((JavascriptExecutor) driver).executeScript(script);
    }

    protected Object executeJavascriptWithReturn(String script) {
        return ((JavascriptExecutor) driver).executeScript(script);
    }

    protected void executeJavascript(String script, WebElement e) {
        ((JavascriptExecutor) driver).executeScript(script, e);
    }

    protected void handleAlert() {
        try {
            switchToAlert().accept();
            logMessage("Alert handled..");
            driver.switchTo().defaultContent();
        } catch (Exception e) {
        }
    }

    protected Alert switchToAlert() {
        WebDriverWait wait = new WebDriverWait(driver, 1);
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    protected void selectProvidedTextFromDropDown(WebElement el, String text) {
        wait.waitForElementToBeVisible(el);
        scrollDown(el);
        Select sel = new Select(el);
        sel.selectByVisibleText(text);
    }

    public int getTimeOut() {
        return Integer.parseInt(getProperty("Config.properties", "timeout"));
    }
    
    protected void selectValueTextFromDropDown(WebElement el, String value) {
        wait.waitForElementToBeVisible(el);
        Select sel = new Select(el);
        sel.selectByValue(value);
    }
    
    protected void verifyPageTitleContains() {
        String expectedPagetitle = getPageTitleFromFile(pageName).trim();
        verifyPageTitleContains(expectedPagetitle);
    }

    /**
     * this method will get page title of current window and match it partially
     * with the param provided
     *
     * @param expectedPagetitle partial page title text
     */
    protected void verifyPageTitleContains(String expectedPagetitle) {
        if (((expectedPagetitle == "") || (expectedPagetitle == null) || (expectedPagetitle
                .isEmpty()))
                && (getProperty("browser").equalsIgnoreCase("chrome"))) {
            expectedPagetitle = getCurrentURL();
        }
        try {
            wait.waitForPageTitleToContain(expectedPagetitle.toString());
            ReportMsg.pass("PageTitle for " + pageName + " contains: '"
                    + expectedPagetitle + "'.");
        } catch (TimeoutException exp) {
            ReportMsg.fail("As actual Page Title for '" + pageName
                    + "' does not contain expected Page Title : '"
                    + expectedPagetitle + "'.");
        }

    }

    public void clickUsingJavaScriptClickEvent(String locatorValue) {
        String path1 = locatorValue;
        String js = "var targetElement = document.evaluate(\"" + path1 + "\",document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;"
                + "targetElement.click();";
        ((JavascriptExecutor) driver).executeScript(js);
    }

    protected void selectProvidedValue(WebElement el, String value) {
        wait.waitForElementToBeVisible(el);
        Select sel = new Select(el);
        sel.selectByValue(value);
    }
    
    protected void scrollDown(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    //TODO: refactor out the usage of this or rework it; hard waits pile up and should be avoided!
    protected void hoverClick(WebElement element) {
        wait.hardWait(2);
        Actions hoverClick = new Actions(driver);
        Actions MenuItems = hoverClick.moveToElement(element);
        wait.hardWait(2);
        MenuItems.click().build().perform();
    }

    public void fireOnClickJsEvent(String elementRef) {
        executeJavascript("var elem = document.getElementById(" + elementRef + ");"
                + "if( document.createEvent ) { var evObj = document.createEvent('MouseEvents');"
                + "    evObj.initEvent( 'click', true, false );"
                + "   elem.dispatchEvent(evObj);"
                + "} else if( document.createEventObject ) {"
                + "    elem.fireEvent('onclick');"
                + "}"
                + "");
    }

    //TODO: refactor out the usage of this or rework it; hard waits pile up and should be avoided!
    protected void click(WebElement getElementWhenVisible) {
        try {
            wait.hardWait(3);
            wait.waitForElementToBeVisible(getElementWhenVisible);
            getElementWhenVisible.click();
        } catch (StaleElementReferenceException ex1) {
            wait.waitForElementToBeVisible(getElementWhenVisible);
            scrollDown(getElementWhenVisible);
            getElementWhenVisible.click();
            logMessage("Clicked Element " + getElementWhenVisible + " after catching Stale Element Exception");
        } catch (Exception ex2) {
            logMessage("Element " + getElementWhenVisible + " could not be clicked! " + ex2.getMessage());
        }
    }

    protected void hover(WebElement element) {
        Actions hoverOver = new Actions(driver);
        hoverOver.moveToElement(element).build().perform();
        hoverOver.perform();
    }
    
    public void logMessage(String msgType, String message){
        ReportMsg.log(msgType, message);
    }
    
    protected void hoverUsingJS(WebElement element) {
        String javaScript = "var evObj = document.createEvent('MouseEvents');"
                + "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                + "arguments[0].dispatchEvent(evObj);";
        ((JavascriptExecutor) driver).executeScript(javaScript, element);
    }

    public void launchSpecificUrl(String url) {
        driver.get(url);
    }
    
    protected void switchToFrameWithOutDefault(WebElement element) {
        wait.waitForElementToBeVisible(element);
        driver.switchTo().frame(element);
        logMessage("Switched to frame without switching to default content");
    }
    
    public void waitTOSync() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    
    protected void waitForSpinnerToDisappear() {
        int i = 0;
        wait.resetImplicitTimeout(5);
        try {
            List<WebElement> spinnerGifs = driver.findElements(By.xpath("//img[contains(@src, '/nb/ui/images/savingAnimation_')]"));
            if (spinnerGifs.size() > 0) {
                for (WebElement spinnerGif : spinnerGifs) {
                    while (spinnerGif.isDisplayed() && i <= AJAX_WAIT) {
                        wait.hardWait(5);
                        i++;
                    }
                }
            }
        } catch (Exception e) {
        }
        wait.resetImplicitTimeout(AJAX_WAIT);
    }
}
