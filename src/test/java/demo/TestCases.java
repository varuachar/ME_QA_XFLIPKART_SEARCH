
package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;

import demo.wrappers.Wrappers;

public class TestCases {

    ChromeDriver driver;
    Wrappers wrapper;

    // Common Flipkart product card locator
    By productCards = By.cssSelector("div[data-id]");

    // Product rating locator
    By ratingLocator = By.cssSelector(".XQDdHH, ._3LWZlK");

    // Product title locator
    By titleLocator = By.cssSelector(
        ".KzDlHZ, .wjcEIp, .syl9yP"
    );

    // Product discount locator
    By discountLocator = By.cssSelector(
        ".UkUFwK, ._3Ay6Sb"
    );

    // Search input locator
    By searchBox = By.name("q");

    @BeforeTest
    public void startBrowser() {

        System.setProperty(
            "java.util.logging.config.file",
            "logging.properties"
        );

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);

        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(
            ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY,
            "build/chromedriver.log"
        );

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        wrapper = new Wrappers(driver);
    }

    // Close Flipkart login popup if it appears
    private void closeLoginPopup() {

        try {
            List<WebElement> closeButtons = driver.findElements(
                By.cssSelector(
                    "span._30XB9F, button[aria-label='Close'], button[aria-label='close']"
                )
            );

            for (WebElement button : closeButtons) {

                if (button.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();",
                        button
                    );
                    break;
                }
            }

            driver.findElement(By.tagName("body"))
                .sendKeys(Keys.ESCAPE);

        } catch (Exception e) {
            System.out.println("No login popup found");
        }
    }

    // Wait until Flipkart search results are loaded
    private void waitForSearchResults() {

        new WebDriverWait(driver, Duration.ofSeconds(20))
            .until(ExpectedConditions.urlContains("/search"));

        closeLoginPopup();

        wrapper.waitForElement(productCards);
    }

    // TestCase01: Washing Machine rating count
    @Test(priority = 1)
    public void testCase01() {

        System.out.println("Starting TestCase01");

        wrapper.navigate("https://www.flipkart.com/");

        closeLoginPopup();

        wrapper.typeElement(searchBox, "Washing Machine");
        wrapper.pressEnter(searchBox);

        waitForSearchResults();

        // Sort products by popularity
        wrapper.clickElement(
            By.xpath("//*[normalize-space()='Popularity']")
        );

        wrapper.waitForElement(productCards);

        List<WebElement> products =
            wrapper.getElements(productCards);

        int count = 0;

        System.out.println(
            "Washing Machines with rating <= 4:"
        );

        for (WebElement product : products) {

            String rating = wrapper.getChildText(
                product,
                ratingLocator
            );

            try {
                double value = Double.parseDouble(
                    rating.replaceAll("[^0-9.]", "")
                );

                if (value <= 4.0 && value > 0) {
                    count++;

                    System.out.println(
                        "Rating: " + rating
                    );
                }

            } catch (NumberFormatException e) {
                // Ignore products without a valid rating
            }
        }

        System.out.println(
            "Total products with rating <= 4: " + count
        );
    }

    // TestCase02: iPhone products with discount above 17%
    @Test(priority = 2)
    public void testCase02() {

        System.out.println("Starting TestCase02");

        wrapper.navigate("https://www.flipkart.com/");

        wrapper.typeElement(searchBox, "iPhone");
        wrapper.pressEnter(searchBox);

        wrapper.waitForElement(productCards);

        List<WebElement> products =
            wrapper.getElements(productCards);

        System.out.println(
            "iPhone products with discount above 17%"
        );

        for (WebElement product : products) {

            String title = wrapper.getChildText(
                product,
                titleLocator
            );

            String discount = wrapper.getChildText(
                product,
                discountLocator
            );

            Matcher matcher = Pattern.compile(
                "(\\d+)\\s*%\\s*off",
                Pattern.CASE_INSENSITIVE
            ).matcher(discount);

            if (matcher.find()) {

                int percentage = Integer.parseInt(
                    matcher.group(1)
                );

                if (percentage > 17) {

                    System.out.println(
                        "Product: " + title
                    );

                    System.out.println(
                        "Discount: " + discount
                    );
                }
            }
        }
    }

    // TestCase03: Coffee Mug products with highest reviews
    @Test(priority = 3)
    public void testCase03() {

        System.out.println("Starting TestCase03");

        wrapper.navigate("https://www.flipkart.com/");

        closeLoginPopup();

        wrapper.typeElement(searchBox, "Coffee Mug");
        wrapper.pressEnter(searchBox);

        waitForSearchResults();

        // Apply 4 stars and above filter
        wrapper.clickElement(
            By.xpath(
                "//*[contains(normalize-space(.),'4') and contains(normalize-space(.),'above')]"
            )
        );

        wrapper.waitForElement(productCards);

        List<WebElement> products =
            wrapper.getElements(productCards);

        List<ProductDetails> productList =
            new ArrayList<>();

        // Collect title, image URL and review count
        for (WebElement product : products) {

            String cardText = wrapper.getElementText(product);

            String title = wrapper.getChildText(
                product,
                titleLocator
            );

            String imageUrl = wrapper.getChildAttribute(
                product,
                By.cssSelector("img"),
                "src"
            );

            int reviews = extractReviewCount(cardText);

            if (!title.isEmpty() && !imageUrl.isEmpty()) {

                productList.add(
                    new ProductDetails(
                        title,
                        imageUrl,
                        reviews
                    )
                );
            }
        }

        // Sort products by review count in descending order
        productList.sort(
            Comparator.comparingInt(
                (ProductDetails p) -> p.reviews
            ).reversed()
        );

        System.out.println(
            "Top 5 Coffee Mugs by review count:"
        );

        int limit = Math.min(5, productList.size());

        for (int i = 0; i < limit; i++) {

            ProductDetails product = productList.get(i);

            System.out.println(
                "Product: " + product.title
            );

            System.out.println(
                "Reviews: " + product.reviews
            );

            System.out.println(
                "Image URL: " + product.imageUrl
            );
        }
    }

    // Extract review count from product card text
    private int extractReviewCount(String text) {

        Matcher matcher = Pattern.compile(
            "([\\d,]+)\\s+Reviews?",
            Pattern.CASE_INSENSITIVE
        ).matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(
                matcher.group(1).replace(",", "")
            );
        }

        matcher = Pattern.compile(
            "([\\d,]+)\\s+Ratings?",
            Pattern.CASE_INSENSITIVE
        ).matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(
                matcher.group(1).replace(",", "")
            );
        }

        return 0;
    }

    // Stores product information for TestCase03
    class ProductDetails {

        String title;
        String imageUrl;
        int reviews;

        ProductDetails(
            String title,
            String imageUrl,
            int reviews
        ) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.reviews = reviews;
        }
    }

    @AfterTest
    public void endTest() {

        if (driver != null) {
            driver.quit();
        }
    }
}
