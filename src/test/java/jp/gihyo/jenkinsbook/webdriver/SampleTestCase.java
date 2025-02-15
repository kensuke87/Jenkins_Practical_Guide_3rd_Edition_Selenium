package jp.gihyo.jenkinsbook.webdriver;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import jp.gihyo.jenkinsbook.page.ResultPage;
import jp.gihyo.jenkinsbook.page.TopPage;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.devicefarm.*;
import software.amazon.awssdk.services.devicefarm.model.*;

public class SampleTestCase {

	private static Properties prop = new Properties();
	private static WebDriver driver;
	private static String URL;
	
	@BeforeClass
	public static void setUpClass() throws IOException {
		prop.load(new FileInputStream("target/test-classes/selenium.properties"));
 		//System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
		//final WebDriver driver = new ChromeDriver();

		String myProjectARN = "arn:aws:devicefarm:us-west-2:421850136157:testgrid-project:5e4732b5-6e55-4e9a-9a50-63aa8166eb79";
	    DeviceFarmClient client  = DeviceFarmClient.builder().region(Region.US_WEST_2).build();
	    CreateTestGridUrlRequest request = CreateTestGridUrlRequest.builder()
	      .expiresInSeconds(300)
	      .projectArn(myProjectARN)
	      .build();
	    CreateTestGridUrlResponse response = client.createTestGridUrl(request);
	    DesiredCapabilities cap = DesiredCapabilities.edge();
	    cap.setCapability("ms:edgeChromium", "true");
	    URL testGridUrl = new URL(response.url());
	    // You can now pass this URL into RemoteWebDriver.
	    driver = new RemoteWebDriver(testGridUrl, cap);


	 	//ChromeOptions options = new ChromeOptions();
		EdgeOptions options = new EdgeOptions();
       		//options.setHeadless(true);
//		options.addArguments("headless");
//		options.addArguments("--no-sandbox");
//       		//driver = new ChromeDriver(options);
//		driver = new EdgeDriver(options);
		URL = System.getenv("appURL");
		
}

	@AfterClass
	public static void tearDownClass() throws IOException {
		driver.quit();
	}

	@Test
	public void test01() {
		driver.get(URL);

		TopPage topPage = new TopPage(driver);
		assertEquals("名字", topPage.getLastNameLabel());
		assertEquals("名前", topPage.getFirstNameLabel());

		assertTrue(topPage.hasLastNameInput());
		assertTrue(topPage.hasFirstNameInput());
		assertTrue(topPage.hasRegistSubmit());
		assertTrue(topPage.hasSearchSubmit());

	}

	@Test
	public void test02() {
		driver.get(URL);

		TopPage topPage = new TopPage(driver);
		topPage.setLastName("Hoge");
		topPage.setFirstName("Foo");
		topPage.searchSubmit();

// 		String greeting = "";
// 		Calendar calendar = Calendar.getInstance();
// 		int hour = calendar.get(Calendar.HOUR_OF_DAY);
// 		if (hour < 12) {
// 			greeting = "Good morning";
// 		} else {
// 			greeting = "Good afternoon";
//         }
		ResultPage resultPage = new ResultPage(driver);
		assertEquals("エラー", resultPage.getText());
	}

	@Test
	public void test03() {
		driver.get(URL);
		TopPage topPage = new TopPage(driver);
		topPage.setLastName("Hoge");
		topPage.setFirstName("Foo");
		topPage.registSubmit();
		ResultPage resultPage = new ResultPage(driver);
		assertEquals("エラー", resultPage.getText());
	}

}
