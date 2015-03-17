package jibeAssessment;

import static org.junit.Assert.assertEquals;

import java.util.Random;
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
 * All three tests should pass whenever it is run.
 * 
 * @author JinyiLi
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class AutoSignupDemo {
	
	private static final String URL = "https://demo.cc.jibe.com/";
	
	// This email address will be used for test1 and test2 with the same value,
	// thus it is declared as static.
	private static String email; 
	
	private WebDriver driver;
	
	@Before
	public void setUp() {
		// Use Firefox browser for the test.
		driver = new FirefoxDriver(); 
		// The maximum time the driver would wait while searching for an element is 10 seconds.
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// Load the web page.
		driver.get(URL); 
		
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
		verifyPageLoaded(driver, URL);
	}
	
	@Test
	/**
	 * Test if the response is correct when the input should yield a successful registration.
	 * @throws InterruptedException
	 */
	public void test1() throws InterruptedException {
		// This loop make sure the email address entered hasn't been registered before.
		while (true) {
			String currURL = driver.getCurrentUrl();
			driver.findElement(By.name("firstName")).sendKeys("Justin"); // Enter first name.
			driver.findElement(By.name("lastName")).sendKeys("Timberlake"); // Enter last name.
			
			// Enter the email address, which was randomly generated.
			email = createRandString();
			driver.findElement(By.name("email")).sendKeys(email); 
			
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
			
			// Verify a new page been loaded.
			verifyPageLoaded(driver, currURL);
			if (driver.getCurrentUrl().equals("https://demo.cc.jibe.com/login-email-sent")) {
				// The URL inside the "if" statement indicate the email address was used before.
				// Quit the browser an set up the test again.
				driver.quit(); 
				setUp(); 
			} else {
				break; // If the email hasn't been registered before, break the loop.
			}
		}
		
		// Since the email hasn't been used before,
		// the new page should have an element with text as "Congratulations".
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
		driver.findElement(By.name("email")).sendKeys(email); 
		// Submit the form.
		driver.findElement(By.name("email")).submit();
		
		// Since the email address has just been registered by test1, 
		// the page should indicate "Account Found" as an element.
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
		// Exit the browser when the test is finished.
		driver.quit();
	}
	
	
	/**
	 * Create a random email address.
	 * @return: randomly created email address.
	 */
	private static String createRandString() {
		int length = 7;
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			// The characters in this string are only small capital letter.
			int randAlphInt = 97 + (int) (new Random().nextFloat() * (122 - 97));
			sb.append((char) randAlphInt);
		}
		return sb.toString() + "@jibe.com";
	}
	
	/**
	 * Verify a new page has been loaded.
	 * @param driver: Web Driver
	 * @param url: The URL of the previous page
	 */
	private void verifyPageLoaded (WebDriver driver, final String url) {
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return !driver.getCurrentUrl().equals(url);
			}
		});
	}

}
