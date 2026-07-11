package com.sentinel.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;  // makes getters setters automatically
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanResult {
    private String text;
    private int riskScore;
    private String verdict;
    private String timestamp;
    private List<String> reasons;
    public ScanResult(String text, int riskScore, String verdict, List<String> reasons)
    {
        this.text=text;
        this.riskScore=riskScore;
        this.verdict=verdict;
        this.reasons=reasons;
    }
}





    //no need for contructor manually lombok makes it
//    public ScanResult (List<String> reasons, int riskScore, String verdict , String timestamp, String text) {
//        this.reasons = reasons;
//
//        this.riskScore = riskScore;
//        this.verdict = verdict;
//        this.timestamp = timestamp;
//        this.text = text;
//    }

