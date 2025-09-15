package com.example.demo.repository;

import com.example.demo.domain.Expense;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    List<Expense> findByUserAndCategory(User user, String category);
    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}