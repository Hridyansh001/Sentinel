package com.sentinel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String text;
    private int riskScore;
    private String verdict;
    private LocalDateTime timestamp;
    @ElementCollection
    private List<String> reasons;
    public Alert(String text, int riskScore, String verdict, List<String> reasons) {

        this.text = text;

        this.riskScore = riskScore;

        this.verdict = verdict;

        this.timestamp = LocalDateTime.now();

        this.reasons = reasons;

    }
}
