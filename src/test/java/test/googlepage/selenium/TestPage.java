package test.googlepage.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class TestPage {
    WebDriver driver;
    String url = "https://news.google.com/";

    @BeforeTest
    public void setTest() {
        System.setProperty ("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver ();
    }

    @AfterTest
    public void tierDown() {
        driver.close ();
    }

    /*Description:
    Display news coverage, aggregated from sources all over the world by Google News.
    Test
    1. Check that page is loaded (choose one of the criteria like header value, JS script loaded etc)
    2. Check that the weather is displayed for your city (like Warsaw, New York etc.)
    3.Check that number of news is more than one
    (optional, after 2 basic tasks completed) */
    @Test
    public void testGooglePage() {
        String weatherLinkXpath = "//a[contains(text(), 'weather.com')]";

        SoftAssert softAssert = new SoftAssert ();

        driver.get (url);

        // 1. the page is loaded
        // as a condition I take the link 'More on weather.com'
        // in the 'Your local weather' rect
        driver.manage ().timeouts ().implicitlyWait (500, TimeUnit.MILLISECONDS);
        WebElement weatherLink = driver.findElement (By.xpath (weatherLinkXpath));

        // 2. the local weather is presented (Your local weather)
        softAssert.assertTrue (weatherLink.isDisplayed (), "The page is not loaded completely");

        // 3. number of news is more than 1
        List<WebElement> newsBlocks = driver.findElements (By.xpath ("//div[@data-n-ham='true']/" +
                "following-sibling::div[@jscontroller and @jsmodel]"));
        softAssert.assertTrue (newsBlocks.size () > 1, "The number of news blocks is less than 2");

        // 4. verify 'the channel' array contains either 'CNBC' or 'CNN' or 'Fox News'
        List<String> newsChannelsExpected = new ArrayList<> (List.of ("CNN", "Fox News", "CNBC", "Yahoo News"));
        List<WebElement> newsChannelsPage = driver.findElements (By.xpath ("//div[@data-n-ham='true']/" +
                "following-sibling::div[@jscontroller and @jsmodel]/.//article/.//div/div"));

        List<String> newsChannelsActual = new ArrayList<> ();

        int sz = newsChannelsPage.size ();
        for (int i = 0; i < sz; i++) {
            newsChannelsActual.add (newsChannelsPage.get (i).getText ());
        }
        newsChannelsActual.removeAll (Collections.singleton (""));

        // verify the actual names of channels contain one of the channel from the given list
        boolean isContains = false;

        for (int i = 0; i < newsChannelsActual.size (); i++) {
            for (int x = 0; x < newsChannelsExpected.size (); x++) {
                String act = newsChannelsActual.get (i);
                String exp = newsChannelsExpected.get (x);
                isContains = act.contains (exp);
                if (isContains) break;
            }
            if (isContains) break;
        }

        softAssert.assertTrue (isContains, "The list of channels from the list doesn't" +
                " contain any channels from the given list");

        softAssert.assertAll ();
    }
}
