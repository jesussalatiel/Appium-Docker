package demo;

import org.testng.annotations.Test;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;

public class Mobile {

	/*
	 * Documentation: https://www.swtestacademy.com/appium-tutorial/
	 * https://morioh.com/p/0ec7c1a6f9b0
	 * https://github.com/budtmo/docker-android?ref=morioh.com&utm_source=morioh.com
	 * https://github.com/budtmo/docker-android/blob/master/
	 * README_APPIUM_AND_SELENIUM.md
	 * https://github.com/budtmo/docker-android/blob/master/docker-compose.yml#L70
	 */

	public AndroidDriver<MobileElement> appium;
	public WebDriverWait wait;
	public WebDriver selenium;
	private int TimeOut = 30;

	/*
	 * Warning: Make sure to load app on the container 
	 * docker cp C:\Users\jesus\eclipse-workspace\Appium-Docker\app\spotify.apk android-container-appium:/opt
	 * 
	 * 
	 * Task: Create a docker compose to load container with the app and volumen pre
	 * loaded
	 * 
	 */

	// Set Container URI -> Selenium/Appium Grid
	private String remoteContainer = "http://localhost:4444/wd/hub";

	@BeforeTest
	public void beforeClass() throws MalformedURLException {
		DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
		// Set emulator name: execute adb devices on device container to see the name
		desiredCapabilities.setCapability("deviceName", "emulator-5554");
		// Set Platform
		desiredCapabilities.setCapability("platformName", "Android");
		// Set UIAutomator to use with Android to execute xpath easier on real devices
		// and emulators
		desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UIAutomator2");
		// Copy app on the container file device and execute from there
		desiredCapabilities.setCapability("app", "/opt/spotify.apk");
		// Reset environment: remove app and install it again
		desiredCapabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
		desiredCapabilities.setCapability(MobileCapabilityType.NO_RESET, false);
		// Set properties to interacting with app elements
		desiredCapabilities.setCapability("appPackage", "com.spotify.music");
		desiredCapabilities.setCapability("appActivity", "com.spotify.music.MainActivity");

		// Create URL
		URL remoteUrl = new URL(remoteContainer);
		// Create Remote Connection to Selenium/Appium Grid
		appium = new AndroidDriver<MobileElement>(remoteUrl, desiredCapabilities);
		// Wait to load driver
		appium.manage().timeouts().implicitlyWait(TimeOut, TimeUnit.SECONDS);
		// Wait to load mobile elements
		wait = new WebDriverWait(appium, 30);

		
		//Define webDriver 
		WebDriverManager.chromedriver().setup();
		selenium = new ChromeDriver();
		// Wait to load driver
		selenium.manage().timeouts().implicitlyWait(TimeOut, TimeUnit.SECONDS);
	}

	@Test
	public void menu() throws InterruptedException {
		
		Thread.sleep(15000);
		// Spotify: Package: com.spotify.music
		appium.launchApp();
				
		// Click on Login
		By loginBy = By.xpath("//*[@text='Sign up free']");
		wait.until(ExpectedConditions.visibilityOfElementLocated(loginBy)).click();

		// Enter user
		By enterUserBy = By.id("com.spotify.music:id/email");
		wait.until(ExpectedConditions.visibilityOfElementLocated(enterUserBy)).sendKeys("test@test.com");

		// Click on Login
		By submitBy = By.id("com.spotify.music:id/email_next_button");
		wait.until(ExpectedConditions.visibilityOfElementLocated(submitBy)).click();

		// Go to Login
		By goToLoginBy = By.id("com.spotify.music:id/button_positive");
		wait.until(ExpectedConditions.visibilityOfElementLocated(goToLoginBy)).click();

		// Print XML to get AppElements
		System.out.println(appium.getPageSource());

		// Validate "Sign up free" Button is Displayed
		By assertMagicLinkBy = By.id("com.spotify.music:id/request_magiclink_lower_button");
		Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(assertMagicLinkBy)).isDisplayed());
	}

	@AfterTest
	public void afterClass() {
		/*if (appium != null) {
			// Remove configuration
			appium.resetApp();
			// Delete driver
			appium.quit();
		}*/
	}

}
