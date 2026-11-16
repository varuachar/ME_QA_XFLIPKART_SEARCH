package demo.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Wrappers {

    private WebDriver driver;
    private WebDriverWait wait;

    public Wrappers(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Wrapper 1: Navigate to a website
    public void navigate(String url) {
        System.out.println("COMMAND: Navigate " + url);
        driver.get(url);
    }

    // Wrapper 2: Type text into an input field
    public void typeElement(By locator, String value) {
        System.out.println("COMMAND: TypeElement " + value);

        WebElement element = wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );

        element.clear();
        element.sendKeys(value);
    }

    // Wrapper 3: Press Enter in an input field
    public void pressEnter(By locator) {
        WebElement element = wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );
        element.sendKeys(Keys.ENTER);
    }

    // Wrapper 4: Click an element
    public void clickElement(By locator) {
    System.out.println("COMMAND: ClickElement " + locator);

    WebElement element = wait.until(
        ExpectedConditions.presenceOfElementLocated(locator)
    );

    ((org.openqa.selenium.JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoView({block:'center'});", element);

    wait.until(
        ExpectedConditions.elementToBeClickable(locator)
    ).click();
}

    // Wrapper 5: Get text from an element
    public String getElementText(By locator) {
        WebElement element = wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );

        return getElementText(element);
    }

    // Wrapper 6: Get text from an existing WebElement
    public String getElementText(WebElement element) {
        String text = element.getText();

        System.out.println("COMMAND: GetElementText");
        System.out.println("RESPONSE: " + text);

        return text;
    }

    // Wrapper 7: Get multiple elements
    public List<WebElement> getElements(By locator) {
        return wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(locator)
        );
    }

    // Wrapper 8: Get child element text
    public String getChildText(WebElement parent, By locator) {
        try {
            WebElement element = parent.findElement(locator);
            return getElementText(element);
        } catch (Exception e) {
            return "";
        }
    }

    // Wrapper 9: Get child element attribute
    public String getChildAttribute(
        WebElement parent, By locator, String attribute
    ) {
        try {
            WebElement element = parent.findElement(locator);
            return element.getAttribute(attribute);
        } catch (Exception e) {
            return "";
        }
    }

    // Wrapper 10: Wait for an element
    public void waitForElement(By locator) {
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    // Wrapper 11: Check whether an element exists
    public boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    // Wrapper 12: Scroll to an element
    public void scrollToElement(WebElement element) {
        org.openqa.selenium.JavascriptExecutor js =
            (org.openqa.selenium.JavascriptExecutor) driver;

        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
