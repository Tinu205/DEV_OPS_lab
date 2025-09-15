package com.example.demo.controller;

import com.example.demo.domain.Expense;
import com.example.demo.domain.User;
import com.example.demo.repository.ExpenseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping
    public String listExpenses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Expense> expenses = expenseRepository.findByUser(user);
        model.addAttribute("expenses", expenses);
        return "expenses/list";
    }

    @GetMapping("/new")
    public String showExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "expenses/form";
    }

    @PostMapping("/save")
    public String saveExpense(@ModelAttribute Expense expense,
                             HttpSession session,
                             @RequestParam String name,
                             @RequestParam String category,
                             @RequestParam BigDecimal amount,
                             @RequestParam LocalDate date,
                             @RequestParam(required = false) String description) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        expense.setUser(user);
        expense.setName(name);
        expense.setCategory(category);
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setDescription(description);
        
        expenseRepository.save(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Optional<Expense> expenseOpt = expenseRepository.findById(id);
        if (expenseOpt.isPresent() && expenseOpt.get().getUser().getId().equals(user.getId())) {
            model.addAttribute("expense", expenseOpt.get());
            return "expenses/form";
        }
        
        return "redirect:/expenses";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Optional<Expense> expenseOpt = expenseRepository.findById(id);
        if (expenseOpt.isPresent() && expenseOpt.get().getUser().getId().equals(user.getId())) {
            expenseRepository.deleteById(id);
        }
        
        return "redirect:/expenses";
    }
}