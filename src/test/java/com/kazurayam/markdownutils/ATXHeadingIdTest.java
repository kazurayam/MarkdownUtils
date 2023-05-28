package com.kazurayam.markdownutils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 */
public class ATXHeadingIdTest {
    String url = "https://kazurayam.github.io/MarkdownUtils";

    private WebDriver driver;
    private static final int timeout = 500;

    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        options.addArguments("remote-allow-origin=*");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
        driver.manage().window().setSize(new Dimension(1000, 800));
        driver.navigate().to(url);
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void testBody(By by) {
        WebElement heading = driver.findElement(by);
        assertNotNull(heading);
        String derived = ATXHeadingId.of(heading.getText());
        String expected = heading.getAttribute("id");
        System.out.println("derived=" + derived + ", expected=" + expected);
        assertEquals(expected, derived);
    }

    @Test
    public void test_simplest() {
        By by = By.xpath("//h3[contains(text(), 'The simplest case')]");
        testBody(by);
    }

    @Test
    public void test_ampersand() {
        By by = By.xpath("//h3[contains(text(), 'issue3 Asciidoc & Markdown')]");
        testBody(by);
    }

    @Test
    public void test_underbar() {
        By by = By.xpath("//h3[contains(text(), 'issue8 Under bar _ character')]");
        testBody(by);
    }

    @Test
    public void test_parentheses() {
        By by = By.xpath("//h3[contains(text(), 'Parentheses ( and ) characters')]");
        testBody(by);
    }

    @Test
    public void test_dot() {
        By by = By.xpath("//h3[contains(text(), 'issue12 Dot . character')]");
        testBody(by);
    }

    @Test
    public void test_hyphen() {
        By by = By.xpath("//h3[contains(text(), 'issue13 Hyphen - character')]");
        testBody(by);
    }

    @Test
    public void test_slash() {
        By by = By.xpath("//h3[contains(text(), 'issue14 Slash / character')]");
        testBody(by);
    }

    @Test
    public void test_colon() {
        By by = By.xpath("//h3[contains(text(), 'issue15 Colon : character')]");
        testBody(by);
    }


}
