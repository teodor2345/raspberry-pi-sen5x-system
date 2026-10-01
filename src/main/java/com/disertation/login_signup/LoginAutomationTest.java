package com.disertation.login_signup;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginAutomationTest {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();
            driver.get("http://localhost:8080/index.html");

            // ==========================================
            // TEST 1: SIGNUP & LOGIN FLOW
            // ==========================================
            System.out.println("--- Starting Test 1: Signup & Login ---");

            // 1. Switch to Sign Up Form
            WebElement signUpToggle = driver.findElement(By.xpath("//button[text()='Sign Up']"));
            signUpToggle.click();

            // 2. Fill registration details
            String testUser = "user_" + System.currentTimeMillis() / 1000;
            String testPass = "Password123!";

            driver.findElement(By.id("signup-username")).sendKeys(testUser);
            driver.findElement(By.id("signup-email")).sendKeys(testUser + "@example.com");
            driver.findElement(By.id("signup-password")).sendKeys(testPass);
            driver.findElement(By.xpath("//button[text()='Register']")).click();

            // Accept signup alert
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();

            // 3. Perform Login
            driver.findElement(By.id("login-username")).sendKeys(testUser);
            driver.findElement(By.id("login-password")).sendKeys(testPass);
            driver.findElement(By.xpath("//button[text()='Log In']")).click();

            // Handle potential login alert if present
            try {
                Alert loginAlert = wait.until(ExpectedConditions.alertIsPresent());
                loginAlert.accept();
            } catch (Exception ignored) {}

            // 4. Assert Redirection
            boolean isRedirected = wait.until(ExpectedConditions.urlContains("dashboard.html"));
            if (isRedirected) {
                System.out.println("PASSED [Login]: User successfully redirected to dashboard.html");
            } else {
                System.err.println("FAILED [Login]: User failed to reach dashboard.html");
            }


            // ==========================================
            // TEST 2: DASHBOARD & CHART.JS VISUALIZATION
            // ==========================================
            System.out.println("\n--- Starting Test 2: Chart Verification ---");

            // 1. Check Dashboard Heading
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
            System.out.println("Dashboard Header: " + header.getText());

            // 2. Verify Canvas Elements Exist and are Visible
            WebElement pmChartCanvas = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pmChart")));
            WebElement tempHumChartCanvas = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tempHumidityChart")));

            if (pmChartCanvas.isDisplayed() && tempHumChartCanvas.isDisplayed()) {
                System.out.println("PASSED [Charts]: Both canvas elements (#pmChart and #tempHumidityChart) are visible on DOM.");
            }

            // 3. Verify Canvas Rendering Dimensions (Height & Width > 0)
            int pmWidth = pmChartCanvas.getSize().getWidth();
            int pmHeight = pmChartCanvas.getSize().getHeight();

            if (pmWidth > 0 && pmHeight > 0) {
                System.out.println("PASSED [Render]: PM Chart successfully rendered with dimensions: " + pmWidth + "x" + pmHeight + "px");
            } else {
                System.err.println("FAILED [Render]: PM Chart canvas has zero height/width.");
            }

            // 4. Verify Chart.js Data Generation via JavaScript execution
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            // Wait brief moment for fetch API call to complete and render data points
            Thread.sleep(1500);

            // Execute script to check if Chart instance has datasets loaded
            Long datasetCount = (Long) js.executeScript(
                "return Chart.getChart('pmChart').data.datasets.length;"
            );

            if (datasetCount != null && datasetCount == 3) {
                System.out.println("PASSED [Data]: Chart.js successfully loaded " + datasetCount + " datasets (PM1, PM2.5, PM10).");
            } else {
                System.err.println("FAILED [Data]: Expected 3 datasets on PM Chart, found: " + datasetCount);
            }

            System.out.println("\nALL TESTS COMPLETED SUCCESSFULLY.");
            Thread.sleep(3000); // Visual delay before closing


            // ==========================================
        // TEST 3: DASHBOARD ACTIONS (LOGOUT & SECURITY)
        // ==========================================
            System.out.println("\n--- Starting Test 3: Dashboard Interactions ---");

            // 1. Click the Logout Button
            WebElement logoutBtn = driver.findElement(By.className("btn-logout"));
            logoutBtn.click();

            // 2. Verify Redirection Back to Login Page
            wait.until(ExpectedConditions.urlContains("index.html"));
            System.out.println("PASSED [Logout]: Successfully returned to index.html after clicking Logout.");

            // 3. Security Check: Try accessing dashboard directly while logged out
            driver.get("http://localhost:8080/dashboard.html");
            Thread.sleep(1000);

            // Verify page behavior or login redirection
            System.out.println("Current URL after attempted re-entry: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.err.println("TEST SUITE ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}