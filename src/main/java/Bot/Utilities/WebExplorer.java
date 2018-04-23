package Bot.Utilities;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import org.openqa.selenium.*;

public class WebExplorer {

    private ChromeDriverService service;
    private WebDriver driver;
    public Robot robot;

    private void createAndStartService() throws IOException {
        File driver;

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            driver = new File("drivers/Windows/chromedriver.exe");
        } else {
            driver = new File("drivers/Linux/chromedriver");
        }

        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(driver)
                .usingAnyFreePort()
                .build();
        service.start();
    }

    public void typeMessage(String characters, int delay) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection( characters );
        clipboard.setContents(stringSelection, null);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        robot.delay(delay);
    }

    private void stopService() {
        service.stop();
    }

    private void createDriver() {
        driver = new RemoteWebDriver(service.getUrl(),
                new ChromeOptions());
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
        robot = new Robot();

        createAndStartService();
        createDriver();

        //Log into facebook, go to the thread
        facebookLogin(email, password);
        goToFacebookThread(threadID);

        //Move mouse into the chat box. Probably not a great solution.
        robot.mouseMove(400, 1000);

        //Double click on the chat box.
        //Needs an extra click to ignore the "Facebook wants to send notifications"
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(200);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        robot.delay(200);

        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(200);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        //Cursor is now in the text box
        System.out.println("Completed login sequence");
    }

    public void stopExploration() {
        stopService();
        quitDriver();
    }
}