package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // GET /api/expenses?month=8&year=2026 OR GET /api/expenses (All history)
    @GetMapping
    public List<Expense> getUserExpenses(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Authentication authentication) {
        
        String username = authentication.getName();

        if (month != null && year != null) {
            return expenseRepository.findByUserAndMonthAndYear(username, month, year);
        }

        return expenseRepository.findByUserUsername(username);
    }

    @PostMapping
    public Expense addExpense(@RequestBody Expense expense, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return "Expense deleted successfully!";
    }
}