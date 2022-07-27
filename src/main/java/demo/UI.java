package demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.time.Duration;

public class UI {

    private WebDriver driver;
    private String URI = "https://www.saucedemo.com/";
    private String nodeURI = "http://localhost:4444";

    private String browser = "Firefox_Lin";

    @Before
    public void setup ( ) throws MalformedURLException, InterruptedException {

        DesiredCapabilities capabilities = new DesiredCapabilities ( );
        ChromeOptions chromeOptions = new ChromeOptions ( );
        FirefoxOptions firefoxOptions = new FirefoxOptions();

        switch (browser){
            case "Chrome_Win":
                chromeOptions.addArguments ( "--headless" );
                chromeOptions.setCapability ( "se:recordVideo", true );
                chromeOptions.setCapability ( "se:timeZone", "US/Pacific" );
                chromeOptions.setCapability ( "se:screenResolution", "1920x1080"   );
                capabilities.setCapability ( ChromeOptions.CAPABILITY , chromeOptions );
                chromeOptions.setCapability ("platformName",Platform.LINUX);
                driver = WebDriverManager.chromedriver().remoteAddress ( nodeURI ).capabilities ( capabilities).browserInDocker ().create ();
                break;
            case "Chrome_Mac":
                chromeOptions.addArguments ( "--headless" );
                //chromeOptions.setCapability("platformName", Platform.MAC);
                chromeOptions.setCapability ( "se:recordVideo", true );
                chromeOptions.setCapability ( "se:screenResolution", "1920x1080"   );
                capabilities.setCapability ( ChromeOptions.CAPABILITY , chromeOptions );
                chromeOptions.setCapability ("platformName",Platform.LINUX);
                driver = WebDriverManager.chromedriver().remoteAddress ( nodeURI ).capabilities ( capabilities).browserInDocker ().create ();
                break;
            case "Firefox_Lin":
                firefoxOptions.setCapability ("platformName",Platform.LINUX);
                capabilities.setCapability ( ChromeOptions.CAPABILITY , firefoxOptions );
                driver = WebDriverManager.firefoxdriver ( ).remoteAddress ( nodeURI ).capabilities ( capabilities ).browserInDocker ().create ();
                break;
        }

        System.out.println ( ((RemoteWebDriver)driver).getCapabilities ().toString () );
        driver.manage ().timeouts ().implicitlyWait ( Duration.ofSeconds ( 60 ) );
        driver.manage ().window ().maximize ();
        driver.navigate ( ).to ( URI );
    }

    @Test
    public void isUserNameDisplayed ( ) {
        Assert.assertTrue ( driver.findElement ( By.id ( "user-name" ) ).isDisplayed ( ) );
        System.out.println ("Title: --------->"+driver.getTitle () );
        driver.findElement ( By.id ( "user-name" )).sendKeys ( "standard_user" );
    }

    @After
    public void tearDown ( ) {
        if ( driver != null ) {
            driver.quit ();
        }
    }
}
