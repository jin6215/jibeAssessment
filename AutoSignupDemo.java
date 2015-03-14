package jibeAssessment;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This program uses Selenium WebDriver API and JUnit to provide automated test
 * to simulate 3 different scenarios in which a candidate signs up on
 * https://demo.cc.jibe.com/ with his/her email address. Different scenarios are
 * created because of the different input of the candidate.
 * 
 * @author JinyiLi
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AutoSignupDemo {
	
	private WebDriver driver;
	private String url;

	@Before
	public void setUp() {
		// Use Firefox browser for the test.
		driver = new FirefoxDriver(); 
		// The maximum time the driver would wait while searching for an element is 10 seconds.
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// Load the web page.
		url = "https://demo.cc.jibe.com/";
		driver.get(url);// 
		
		/**
		 * The FirefoxDriver object will throw a WebDriverException the first
		 * time it tries to find an element, even if the correct location is
		 * provided. But after catching that exception, finding an element with
		 * the correct location provided works perfectly well. Therefore I wrote
		 * this try/catch block to bypass this exception. I am really confused
		 * about why it has to work like this, and I haven't find any convincing
		 * explanation online yet.
		 */
		try {
			driver.findElement(By.id("email")).click();
		} catch (WebDriverException e) {
			driver.findElement(By.id("email")).click();
		}
		
		// Verify the new page has been loaded before the test.
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return !driver.getCurrentUrl().equals(url);
			}
		});
	}
	
	@Test
	/**
	 * Test if the response is correct when submit the form with the appropriate input.
	 * @throws InterruptedException
	 */
	public void test1() throws InterruptedException {
		driver.findElement(By.name("firstName")).sendKeys("Justin"); // Enter first name.
		driver.findElement(By.name("lastName")).sendKeys("Timberlake"); // Enter last name.
		driver.findElement(By.name("email")).sendKeys("jtimberlake@jibe.com"); // Enter the email address
		
		// Enter New York, NY as the Location, which will be chosen from the popped out item
		driver.findElement(By.cssSelector("input[placeholder='Location']")).sendKeys("New York, NY");
		// Put the thread into sleep for 0.2 second to let the invisible item show.
		Thread.sleep(200); 
		// Click to choose the popped out item.
		driver.findElement(By.xpath("//tags-input[@placeholder='Location']/descendant::li")).click();
		
		// Enter skills, the comma let a small box come out to contain the item .
		driver.findElement(By.cssSelector("input[placeholder='Search for skills']")).sendKeys("Falsetto Vocalism,");
		driver.findElement(By.cssSelector("input[placeholder='Search for skills']")).sendKeys("Music Programming,");
		
		// Submit the form.
		driver.findElement(By.cssSelector("input[placeholder='Search for skills']")).submit();
		
		// The new page should have an element with text as "Congratulations".
		// The test is considered successful when it shows.
		assertEquals(driver.findElement(By.xpath("//div[@class='success-container ng-scope']/h1")).getText(), "Congratulations");
	}
	
	@Test
	/**
	 * Test if the response is correct when submit the form with the email address being registered before.
	 */
	public void test2() {
		// Enter first name.
		driver.findElement(By.name("firstName")).sendKeys("Justin"); 
		// Enter last name.
		driver.findElement(By.name("lastName")).sendKeys("Timberlake"); 
		// Enter the email address, which has been used during test1.
		driver.findElement(By.name("email")).sendKeys("jtimberlake@jibe.com"); 
		// Submit the form.
		driver.findElement(By.name("email")).submit();
		
		// Since the email address has been used before, the page should indicate "Account Found" as an element.
		// The test is considered successful when it shows.
		assertEquals(driver.findElement(By.xpath("//div[@class='login-email-sent ng-scope']/h1")).getText(), "Account Found");
	}
	
	@Test
	/**
	 * Test if the response is correct when submit the form without the email address entered.
	 */
	public void test3() {
		driver.findElement(By.name("firstName")).sendKeys("Justin"); // Enter first name.
		driver.findElement(By.name("lastName")).sendKeys("Timberlake"); // Enter last name.
		driver.findElement(By.name("lastName")).submit(); // Submit the form.
		
		// If the form is submitted without any one of the required fields,
		// an element's class name should change into "validate ng-scope".
		// The test is considered successful when it happens.
		assertEquals(driver.findElement(By.xpath("//div[@src=\"'views/validatejoin.html'\"]/ul")).getAttribute("class"), "validate ng-scope");
	}
	
	@After
	public void tearDown() throws Exception {
		// Exit the browser when all the tests are finished.
		driver.quit();
	}
	
}
