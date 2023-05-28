package com.kazurayam.markdownutils;

import com.kazurayam.subprocessj.Subprocess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
 * https://kazurayam.github.io/MarkdownUtils/ATXHeadingTest.md
 */
public class ATXHeadingTest {
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

    @Test
    public void test_simplest() {
        String content = "The simplest case";
        By by = By.xpath("//h3[contains(text(), content)]");
        WebElement heading = driver.findElement(by);
        assertNotNull(heading);
        String derivedId = ATXHeadingId.of(heading.getText());
        String expectedId = heading.getAttribute("id");
        assertEquals(expectedId, derivedId);
    }
}
