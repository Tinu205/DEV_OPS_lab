package com.example.demo.controller;

import com.example.demo.domain.Expense;
import com.example.demo.domain.User;
import com.example.demo.repository.ExpenseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Expense> expenses = expenseRepository.findByUser(user);
        
        // Calculate total expenses
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate expenses by category
        Map<String, BigDecimal> expensesByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                    Expense::getCategory,
                    Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));
        
        // Get recent expenses
        List<Expense> recentExpenses = expenses.stream()
                .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                .limit(5)
                .collect(Collectors.toList());
        
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("expensesByCategory", expensesByCategory);
        model.addAttribute("recentExpenses", recentExpenses);
        
        return "dashboard";
    }
}