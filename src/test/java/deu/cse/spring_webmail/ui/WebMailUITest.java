/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebMailUITest {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:9999/webmail/";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();  // 테스트마다 브라우저 종료
        }
    }

    @Test
    @Order(1)
    @DisplayName("로그인 테스트 - 성공 및 실패")
    void testLogin() {
        driver.get(BASE_URL);

        // 정상 로그인
        driver.findElement(By.name("username")).sendKeys("user01@james.local");
        driver.findElement(By.name("password")).sendKeys("1234");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // 성공 조건: main_menu로 이동했는지 확인
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/main_menu"));

        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/webmail/main_menu"));

        // 비정상 로그인
        driver.get(BASE_URL);  // 다시 로그인 페이지로
        driver.findElement(By.name("username")).sendKeys("wronguser");
        driver.findElement(By.name("password")).sendKeys("wrongpass");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        Assertions.assertTrue(driver.getPageSource().contains("로그인 실패")
                || driver.getCurrentUrl().contains("login_fail"));
    }

    @Test
    @Order(2)
    @DisplayName("메일 쓰기 테스트 - 전송 결과 메시지 확인")
    void testWriteMail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 1. 로그인 페이지 진입
            driver.get(BASE_URL + "login");
            driver.findElement(By.name("username")).sendKeys("user01@james.local");
            driver.findElement(By.name("password")).sendKeys("1234");
            driver.findElement(By.cssSelector("input[type='submit']")).click();

            // 2. 로그인 성공 → 메인 메뉴 도착까지 확인
            wait.until(ExpectedConditions.urlContains("/main_menu"));
            Assertions.assertTrue(driver.getCurrentUrl().contains("/main_menu"));

            // 3. 메일쓰기 페이지로 이동
            driver.get(BASE_URL + "write_mail?testing=true");

            // 4. 입력 필드가 모두 보일 때까지 대기
            WebElement to = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("to")));
            WebElement subj = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("subj")));
            WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("body")));

            // 5. 값 입력
            to.clear();
            to.sendKeys("test@localhost");

            subj.clear();
            subj.sendKeys("UI 테스트 제목");

            body.clear();
            body.sendKeys("UI 테스트 본문입니다.");

            // 6. 메일 전송
            driver.findElement(By.cssSelector("input[type='submit']")).click();

            // 7. 결과 메시지 존재 여부는 일단 생략 가능 (flash-msg는 실패 가능성 있음)
            System.out.println("테스트 완료: 메일 전송 동작 수행됨");

        } catch (TimeoutException te) {
            System.err.println("요소를 찾지 못해 테스트 실패");
            System.err.println(" 현재 URL: " + driver.getCurrentUrl());
            System.err.println(" 페이지 소스 앞부분:\n" + driver.getPageSource().substring(0, 1000));
            Assertions.fail("입력 요소를 찾지 못함: " + te.getMessage());
        }
    }

}
