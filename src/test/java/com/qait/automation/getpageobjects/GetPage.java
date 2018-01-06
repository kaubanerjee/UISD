package com.qait.automation.getpageobjects;

import static com.qait.automation.getpageobjects.ObjectFileReader.getELementFromFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.qait.automation.utils.ReportMsg;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.testng.Assert.*;
import org.testng.Reporter;

public class GetPage extends BaseUi {

    public WebDriver webdriver;
    public String pageName;
    String windowHandle;

    public GetPage(WebDriver driver, String pageName) {
        super(driver, pageName);
        this.webdriver = driver;
        this.pageName = pageName;
    }

    public void pageRefresh1() {
    	waitTOSync();
        driver.navigate().refresh();
        handleAlert();
        logMessage("[INFO]: PAGE GOT REFRESHED...TAKING YOU TO HOME PAGE SO SIT TIGHT AND CHILL!!!");
        wait.waitForPageToLoadCompletely();
        waitTOSync();
    }


    protected void switchToNewWindow() {
        windowHandle = driver.getWindowHandle();
        for (String winHandle : driver.getWindowHandles()) {
            // switch focus of WebDriver to the next found window handle (that's
            // your newly opened window)
            driver.switchTo().window(winHandle);
        }
    }

    protected void close_Child_And_Move_ToParent() {
        driver.close();
        driver.switchTo().window(windowHandle);
    }

    protected WebElement getElementWhenVisible(String elementToken) {
        return getElementWhenVisible(elementToken, "");
    }
   

    protected WebElement elementWithoutVisibility(String elementToken) {
        return elementNoVisibility(elementToken, "");
    }
    
    protected WebElement elementNoVisibility(String elementToken, String replacement) throws NoSuchElementException {
        WebElement elem = null;
        try {
            elem = webdriver.findElement(getLocator(elementToken, replacement));
        } catch (NoSuchElementException excp) {
            fail(logMessage("[ASSERT FAILED]: Element " + elementToken + " not found on the webPage !!!"));
        }
        return elem;
    }

    public void switchToNestedFrames(String frameNames) {
        switchToDefaultContent();
        String[] frameIdentifiers = frameNames.split(":");
        for (String frameId : frameIdentifiers) {
            wait.waitForFrameToBeAvailableAndSwitchToIt(getLocator(frameId
                    .trim()));
            ReportMsg.info("Switch to "+frameId+" Frame");
        }
    }
    
    protected WebElement getElementWhenVisible(String elementToken, String replacement) throws NoSuchElementException {
        WebElement foundElement = null;
        try {
            By elementLocator = getLocator(elementToken, replacement);
            WebElement webElement = webdriver.findElement(elementLocator);
            foundElement = wait.waitForElementToBeVisible(webElement);
        } catch (NoSuchElementException excp) {
            fail(logMessage("[ASSERT FAILED]: Element " + elementToken + " not found on the webPage !!!"));
        } catch (NullPointerException npe) {
            fail("[UNHANDLED EXCEPTION]: " + npe.getLocalizedMessage());
        }
        return foundElement;
    }

    protected List<WebElement> executeJavascriptWithReturnelement(String script) {
        return (List<WebElement>) ((JavascriptExecutor) driver).executeScript(script);
    }
    
    public boolean element_visibility(String elementToken) {
        return element_boolean(elementToken, "");
    }

    public boolean element_boolean(String elementToken, String replacement) throws NoSuchElementException {
        boolean flag = false;
        wait.resetImplicitTimeout(2);
        wait.hardWait(2);
        try {
            @SuppressWarnings("unused")
            WebElement elem = webdriver.findElement(getLocator(elementToken, replacement));
            flag = true;
        } catch (Exception excp) {
        }
        wait.resetImplicitTimeout(getTimeOut());
        return flag;
    }

    public void selectDropDownValues(WebElement e, String value) {
        Select dropdown = new Select(e);
        dropdown.selectByVisibleText(value);
        Reporter.log(value + " is selected");
    }
    
    public void sendKeysUsingActionBuilder(WebElement element, String text) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.click();
        actions.sendKeys(text);
        actions.build().perform();
    }
    
    public void selectDropDownByIndex(WebElement e, int idex) {
        Select dropdown = new Select(e);
        dropdown.selectByIndex(idex);
        Reporter.log(idex + " is selected");
    }
    
    
    
    public void selectTypeOfCourseByValue(WebElement e, String value){
    	wait.waitForPageToLoadCompletely();
    	Select dropdown = new Select(e);
        dropdown.selectByValue(value);
        Reporter.log(value +"is Selected");
        	
    }
    
    protected List<WebElement> elements(String elementToken, String replacement) {
        return webdriver.findElements(getLocator(elementToken, replacement));
    }

    protected List<WebElement> elementsWithoutVisibility(String elementToken) {
        return webdriver.findElements(getLocator(elementToken));
    }

    protected List<WebElement> elementsWithoutVisibility(String elementToken, String replacement) {
        return webdriver.findElements(getLocator(elementToken, replacement));
    }

    protected List<WebElement> elementsFromElement(WebElement el, String elementToken, String replacement) {
        return wait.waitForElementsToBeVisible(el.findElements(getLocator(elementToken, replacement)));
    }

    protected List<WebElement> elements(String elementToken) {
        return elements(elementToken, "");
    }

    protected List<WebElement> elementsNoVisibility(String elementToken) {
        return elementsWithoutVisibility(elementToken, "");
    }

    protected List<WebElement> elementsFromElement(WebElement el, String elementToken) {
        return elementsFromElement(el, elementToken, "");
    }

    protected boolean isElementDisplayed(String elementName, String elementTextReplace) {
        wait.waitForElementToBeVisible(getElementWhenVisible(elementName, elementTextReplace));
        boolean result = getElementWhenVisible(elementName, elementTextReplace).isDisplayed();
        assertTrue(result, "[ASSERT FAILED]: element '" + elementName + "' with text " + elementTextReplace
                + "' is not displayed.");

        return result;
    }

    protected boolean verifyElementText(String elementName, String expectedText) {
        try {
            assertEquals(
                    (getElementWhenVisible(elementName).getText().toLowerCase().trim()).contains(expectedText.toLowerCase().trim()),
                    true);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    protected boolean isElementDisplayedHidden(WebElement element) {

        boolean result = element.isDisplayed();

        assertTrue(result, ReportMsg.failForAssert(" element  is not displayed."));
        ReportMsg.pass("element is displayed.");
        return result;
    }
    
    protected boolean verifyElementTextContains(WebElement elementName, String expectedText) {
        try {
            wait.waitForElementToBeVisible(elementName);
            assertThat(elementName.getText().toLowerCase().trim(), containsString(expectedText.toLowerCase().trim()));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
    
    protected void waitForElementPresent(String ele) {
        for (int second = 0;; second++) {
            if (second >= AJAX_WAIT) {
                Reporter.log("element not present");
                break;
            } else {
                wait.resetImplicitTimeout(3);
                try {
                    getElementWhenVisible(ele);
                    wait.resetImplicitTimeout(AJAX_WAIT);
                    Reporter.log(ele + " is present");
                    break;
                } catch (Exception ee) {
                    wait.hardWait(2);
                }
            }
        }
    }

    protected WebElement element(String elementToken, String replacement1, String replacement2) {
        WebElement elem = null;
        try {
            elem = wait.waitForElementToBeVisible(webdriver
                    .findElement(getLocator(elementToken, replacement1, replacement2)));
        }catch (NoSuchElementException excp) {
            ReportMsg.fail("No such Element Found Exception caught!!!!");
            ReportMsg.fail("Element " + elementToken + " not found on the " + this.pageName + " !!!");
        } catch (Exception npe) {
            ReportMsg.fail(npe.getLocalizedMessage());
            ReportMsg.fail("General Exception cought Exception caught!!!!");
            ReportMsg.fail("Element " + elementToken + " not found on the " + this.pageName + " !!!");
        }
        return elem;
    }
    
    protected boolean isElementDisplayed(String elementName) {
        WebElement result = getElementWhenVisible(elementName);

        if (result != null)
            return result.isDisplayed();

        return false;
    }

    protected boolean isElementDisplayed(WebElement element) {
        if (element != null)
            return element.isDisplayed();
        return false;
    }

    protected boolean isElementEnabled(String elementName, boolean expected) {
        boolean result = expected && getElementWhenVisible(elementName).isEnabled();
        return result;
    }

    protected boolean isElementEnabled(WebElement elementName, boolean expected) {
        boolean result = expected && elementName.isEnabled();
        return result;
    }

    protected By getLocator(String elementToken) {
        return getLocator(elementToken, "");
    }

    protected By getLocator(String elementToken, String replacement) {
        String[] locator = getELementFromFile(this.pageName, elementToken);
        locator[2] = locator[2].replaceAll("\\$\\{.+\\}", replacement);
        return getBy(locator[1].trim(), locator[2].trim());
    }

    protected By getLocator(String elementToken, String replacement1, String replacement2) {
        String[] locator = getELementFromFile(this.pageName, elementToken);
        locator[2] = StringUtils.replace(locator[2], "$", replacement1);
        locator[2] = StringUtils.replace(locator[2], "%", replacement2);
        return getBy(locator[1].trim(), locator[2].trim());
    }

    private By getBy(String locatorType, String locatorValue) {
        switch (Locators.valueOf(locatorType)) {
            case id:
                return By.id(locatorValue);
            case xpath:
                return By.xpath(locatorValue);
            case css:
                return By.cssSelector(locatorValue);
            case name:
                return By.name(locatorValue);
            case classname:
                return By.className(locatorValue);
            case linktext:
                return By.linkText(locatorValue);
            default:
                return By.id(locatorValue);
        }
    }

    public void SendText(String elementToken, String keyToEnter) {
        getElementWhenVisible(elementToken).click();
        getElementWhenVisible(elementToken).clear();
        getElementWhenVisible(elementToken).sendKeys(keyToEnter);
    }

    public void typeOnElementUsingActionBuilder(String elementName, String text) {
        Actions builder = new Actions(driver);
        getElementWhenVisible(elementName).clear();
        builder.moveToElement(getElementWhenVisible(elementName)).sendKeys(text).build().perform();
    }

    public void typeOnElementUsingActionBuilder(WebElement elementName, String text) {
        Actions builder = new Actions(driver);
        elementName.clear();
        builder.moveToElement(elementName).sendKeys(text).build().perform();
    }

    public void typeOnElementUsingActionBuilderWithoutClear(String elementName, String text) {
        Actions builder = new Actions(driver);
        builder.moveToElement(getElementWhenVisible(elementName)).sendKeys(text).build().perform();
    }

    public void typeOnElementUsingActionBuilderWithoutClear(String elementName, String replacement, String text) {
        Actions builder = new Actions(driver);
        builder.moveToElement(getElementWhenVisible(elementName, replacement)).sendKeys(text).build().perform();
    }
    
    
   public void clickElementUsingJavaScript(WebElement element){
	   JavascriptExecutor executor = (JavascriptExecutor)driver;
	   executor.executeScript("arguments[0].click();", element);
   }
   
   
   public void switechToFrameUsingWebElement(WebElement element){
	   
	   driver.switchTo().frame(element);
	   
	   
   }
    
    
   public void mouseHoverToWebElement(WebElement element){
	Actions builder = new Actions(driver);
	builder.moveToElement(element).build().perform();
   }
	   
   
   public void waitForElement(WebElement element){
	   
	   WebDriverWait wait = new WebDriverWait(driver, 2);
	   wait.until(ExpectedConditions.elementToBeClickable(element));
	   element.click();
   }
	
    public void captureImg(String ele) throws IOException{
    	  wait.hardWait(1);
    	  File file = tks.takeScreenShotForCucumber();
    	  BufferedImage  fullImg = ImageIO.read(file);

    	  // Get the location of element on the page
    	  Point point = getElementWhenVisible(ele).getLocation();

    	  // Get width and height of the element
    	  int eleWidth = getElementWhenVisible(ele).getSize().getWidth();
    	  int eleHeight = getElementWhenVisible(ele).getSize().getHeight();

    	  // Crop the entire page screenshot to get only element screenshot
    	  BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(),
    	      eleWidth, eleHeight);
    	  ImageIO.write(eleScreenshot, "png", file);

    	  // Copy the element screenshot to disk
    	  File screenshotLocation = new File("./target/element/ele.png");
    	  FileUtils.copyFile(file, screenshotLocation);
    	 }

}
