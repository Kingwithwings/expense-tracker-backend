package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // Fetch all expenses for a user
    List<Expense> findByUserUsername(String username);

    // Fetch expenses for a user filtered by specific month and year
    @Query("SELECT e FROM Expense e WHERE e.user.username = :username AND FUNCTION('MONTH', e.date) = :month AND FUNCTION('YEAR', e.date) = :year ORDER BY e.date DESC")
    List<Expense> findByUserAndMonthAndYear(
        @Param("username") String username, 
        @Param("month") int month, 
        @Param("year") int year
    );
}