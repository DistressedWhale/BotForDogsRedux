import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;

import org.openqa.selenium.*;

public class WebExplorer {

    private ChromeDriverService service;
    private WebDriver driver;
    public MrRobot robot;

    private void createAndStartService() throws IOException {
        try {
            service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File("drivers/Windows/chromedriver.exe"))
                    .usingAnyFreePort()
                    .build();
            service.start();
        } catch (IOException e) {
            throw e;
        }
    }

    private void stopService() {
        service.stop();
    }

    public void createDriver() {
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());
    }

    private void quitDriver() {
        driver.quit();
    }

    private void facebookLogin(String email, String password) {
        driver.get("https://www.facebook.com");
        WebElement emailBox = driver.findElement(By.name("email"));
        WebElement passwordBox = driver.findElement(By.name("pass"));

        emailBox.sendKeys(email);
        passwordBox.sendKeys(password);
        passwordBox.submit();
    }

    private void goToFacebookThread(String threadID) {
        driver.get("https://www.facebook.com/messages/t/" + threadID);
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public void logIn(String email, String password, String threadID) throws AWTException, IOException {
        System.out.println("Attempting to log in to thread " + threadID);
        robot = new MrRobot();

        createAndStartService();
        createDriver();

        //Log into facebook, go to the thread
        facebookLogin(email, password);
        goToFacebookThread(threadID);

        //Move mouse into the chat box. Probably not a great solution.
        robot.robo.mouseMove(400, 1000);

        //Double click on the chat box.
        //Needs an extra click to ignore the "Facebook wants to send notifications"
        robot.robo.mousePress(InputEvent.BUTTON1_MASK);
        robot.robo.delay(100);
        robot.robo.mouseRelease(InputEvent.BUTTON1_MASK);

        robot.robo.delay(100);

        robot.robo.mousePress(InputEvent.BUTTON1_MASK);
        robot.robo.delay(100);
        robot.robo.mouseRelease(InputEvent.BUTTON1_MASK);

        //Cursor is now in the text box
        System.out.println("Done");
    }

    public void stopExploration() {
        stopService();
        quitDriver();
    }
}