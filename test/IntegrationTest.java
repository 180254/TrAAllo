import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class IntegrationTest {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("webdriver.chrome.driver",
                "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
    }

    @Test
    public void appStart() {
        running(testServer(4000, fakeApplication(inMemoryDatabase())), ChromeDriver.class, browser -> {
            browser.getDriver().get("http://127.0.0.1:4000");
            assertTrue(browser.pageSource().contains("TrAAllo"));
        });
    }

    @Test
    public void afterRegisterICanLogIn() {
        running(testServer(4000, fakeApplication(inMemoryDatabase())), ChromeDriver.class, browser -> {
            browser.goTo("http://127.0.0.1:4000/signup");
            browser.getDriver().findElement(By.id("username")).sendKeys("nowyUzytkownik33");
            browser.getDriver().findElement(By.id("password")).sendKeys("bardzoSlabeHaslo1");
            browser.getDriver().findElement(By.tagName("form")).submit();

            browser.goTo("http://127.0.0.1:3333/login");
            browser.getDriver().findElement(By.id("username")).sendKeys("nowyUzytkownik33");
            browser.getDriver().findElement(By.id("password")).sendKeys("bardzoSlabeHaslo1");
            browser.getDriver().findElement(By.tagName("form")).submit();

            assertTrue(browser.pageSource().contains("You're logged in!"));
        });
    }
}
