package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

login.html
<!DOCTYPE html>
<html>
<head>
 <title>Simple Login</title>
</head>
<body>
 <h2>Login</h2>
 <form action="/login" method="post">
 <label>Username:</label><br>
 <input type="text" name="username"><br><br>
 <label>Password:</label><br>
 <input type="password" name="password"><br><br>
 <button type="submit">Login</button>
</form>
</body>
</html>
User.java
@Entity
@Table(name = "login")
public class User {
 @Id

 private String username;
 private String password;
 // Constructors
 public User() {}
 public User(String username, String password) {
 this.username = username;
 this.password = password;
 }
 // Getters & Setters

 public String getUsername() {
 return username;
 }
 public void setUsername(String username) {
 this.username = username;
 }
 public String getPassword() {
 return password;
 }
 public void setPassword(String password) {
 this.password = password;
 }
}
UserRepository.java
public interface UserRepository extends JpaRepository<User, String> {
 User findByUsernameAndPassword(String username, String password);
}
LoginController.java
@Controller
public class LoginController {
 @Autowired
 private UserRepository userRepository;
 @GetMapping("/")
 public String home() {
 return "home.html";
 }
 @PostMapping("/login")
 @ResponseBody
 public String login(@RequestParam String username, @RequestParam String password) {
 User user = userRepository.findByUsernameAndPassword(username, password);
 if (user != null) {
 return "Login Successful! Welcome " + user.getUsername();
 } else {
 return "Invalid username or password";
 }
 }
}
LoginApplication.java
@SpringBootApplication
public class LoginApplication {
 public static void main(String[] args) {
 SpringApplication.run(LoginApplication.class, args);
 }
}
application.properties
spring.application.name=login
spring.datasource.url=jdbc:mysql://localhost:3306/Demo
spring.datasource.username=root
spring.datasource.password=Password@123
// Draw the table in the database
pom.xml
</parent>
 <groupId>com.example</groupId>
 <artifactId>login</artifactId>
 <version>0.0.1-SNAPSHOT</version>
 <name>login</name>
<dependencies>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-data-jpa</artifactId>
 </dependency>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-web</artifactId>
 </dependency>
 <dependency>
 <groupId>com.mysql</groupId>
 <artifactId>mysql-connector-j</artifactId>
 <scope>runtime</scope>
 </dependency>
</dependencies>
For Testing
LoginControllerTest.java
@WebMvcTest(LoginController.class)
public class LoginControllerTest {
 @Autowired
 private MockMvc mockMvc;
 @MockBean
 private UserRepository userRepository;
 // Test 1: Page load test
 @Test
 public void testLoginPageLoadsSuccessfully() throws Exception {
 mockMvc.perform(get("/"))
 .andExpect(status().isOk())
 .andExpect(view().name("login.html")); // Expecting login.html view
 }
 // Test 2: Successful login
 @Test
 public void testLoginSuccess() throws Exception {
 // Given
 User mockUser = new User();
 mockUser.setUsername("john");
 mockUser.setPassword("1234");
 when(userRepository.findByUsernameAndPassword("john", "1234")).thenReturn(mockUser);
 // When & Then
 mockMvc.perform(post("/login")
 .param("username", "john")
 .param("password", "1234"))
 .andExpect(status().isOk())
 .andExpect(content().string("Login Successful! Welcome john"));
 verify(userRepository, times(1)).findByUsernameAndPassword("john", "1234");
 }
 // Test 3: Invalid login
 @Test
 public void testLoginFailure() throws Exception {
 when(userRepository.findByUsernameAndPassword("wrongUser",
"wrongPass")).thenReturn(null);
 mockMvc.perform(post("/login")
 .param("username", "wrongUser")
 .param("password", "wrongPass"))
 .andExpect(status().isOk())
 .andExpect(content().string("Invalid username or password"));
 verify(userRepository, times(1)).findByUsernameAndPassword("wrongUser", "wrongPass");
 }
}
