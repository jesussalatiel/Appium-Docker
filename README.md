# Demo - Appium-Docker

INTRODUCTION
------------

This example is to see how it works the appium interaction with selenium grid using docker containers and docker compose in Java 8 language on Windows SO
if you prefers you can set the requirements on you so prefers. 

REQUIREMENTS
---------------------
This module requires the following modules:
* [Docker](https://docs.docker.com/desktop/install/windows-install/)
* [Java 8](https://www.java.com/en/download/manual.jsp)
* [Maven](https://maven.apache.org/download.cgi)


```yml
version: "3"
services:
  selenium_hub:
    image: selenium/hub:3.14.0-curium
    ports:
      #Expose port: 4445 to connect with Java
      - "4445:4444"

  # Appium Docker 
  real_device:
    image: appium/appium
    depends_on:
      - selenium_hub
    #network_mode: "services:selenium_hub"
    privileged: true
    volumes:
      - /dev/bus/usb:/dev/bus/usb #Allows external device connections 
      - ~/.android:/root/.android #Always allow this computer screenshot
    environment:
       #Allows to connect Appium to Selenium Grid
      - CONNECT_TO_GRID=true
      #Define the container connecting
      - SELENIUM_HOST=selenium_hub
      # Enable it for web device testing
      #- BROWSER_NAME=chrome

  #Define Environments 
  #Android 7.1 - Nexus 5
  nexus_7.1.1:
    image: budtmo/docker-android-x86-7.1.1
    privileged: true
    # Increase scale number if needed
    scale: 1
    depends_on:
      - selenium_hub
      - real_device
    ports:
      - "6082:6080"
    volumes:
      - ./app/spotify.apk:/otp
      #- ./video-nexus_7.1.1:/tmp/video
    environment:
      - DEVICE=Nexus 5
      - CONNECT_TO_GRID=true
      - APPIUM=true
      - SELENIUM_HOST=selenium_hub
      - AUTO_RECORD=trueockerfile

```

Here is an example of capabilities to connect docker and selenium grid:

Warning: Make sure to copy the apk on the android devices

```java
	@Before
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
	}
```
To run specific docker compose 
```
docker-compose -f .\docker-dynamic-grid.yaml up
```


REFERENCES
---------------------
This module requires the following modules:
* [Appium Tutorial Step by Step Appium Automation](https://www.swtestacademy.com/appium-tutorial/)
* [Docker Android: Docker Solution with NoVNC Supported and Video](https://morioh.com/p/0ec7c1a6f9b0)
* [Docker Android Repository](https://github.com/budtmo/docker-android?ref=morioh.com&utm_source=morioh.com)
* [How to setting Appium Server and Selenium Grid](https://github.com/budtmo/docker-android/blob/master/README_APPIUM_AND_SELENIUM.md)
* [Docker Compose Example](https://github.com/budtmo/docker-android/blob/master/docker-compose.yml#L70)
* [Grid v4 - Docker Compose - Example](https://github.com/SeleniumHQ/docker-selenium/blob/trunk/docker-compose-v3-full-grid.yml)
* [Docker images for the Selenium Grid Server](https://github.com/SeleniumHQ/docker-selenium#docker-compose)


ISSUES
----------------------------------------------------------------------
There is an issue with Appium and Selenium Integration on Grid 4

When docker.socket is locked run this command on container: 
```
chmod 0777 /var/run/docker.sock
```