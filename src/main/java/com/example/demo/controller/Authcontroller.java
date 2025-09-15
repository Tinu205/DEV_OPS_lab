package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {
        
        Optional<User> userOpt = userRepository.findByUsernameAndPassword(username, password);
        
        if (userOpt.isPresent()) {
            session.setAttribute("user", userOpt.get());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String username,
                                     @RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword,
                                     Model model) {
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }
        
        // Create new user
        User newUser = new User(username, email, password);
        userRepository.save(newUser);
        
        model.addAttribute("success", "Registration successful! Please login.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}