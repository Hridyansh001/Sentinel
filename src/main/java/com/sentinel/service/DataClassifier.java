package com.sentinel.service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class DataClassifier {

    public static final Pattern AADHAAR = Pattern.compile("\\b[0-9]{4}\\s?[0-9]{4}\\s?[0-9]{4}\\b");
    public static final Pattern CreditCard = Pattern.compile("^[0-9]{13,19}$");
    public static final Pattern PANCARD = Pattern.compile("\\b[A-Z]{5}[0-9]{4}[A-Z]{1}\\b");
    private static final Pattern API_KEY = Pattern.compile("\\b(sk-|ghp_|AIza|Bearer\\s)[A-Za-z0-9_\\-]{20,}\\b");
    private static final Pattern EMAIL = Pattern.compile("\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b");
    private static final Pattern MOBILE = Pattern.compile("\\b[6-9][0-9]{9}\\b");
    private static final Pattern UPI = Pattern.compile("\\b[a-zA-Z0-9.\\-_]{2,256}@(okaxis|okhdfcbank|okicici|oksbi|ybl|ibl|axl|waicici|paytm|apl|upi)\\b");
    private static final Pattern PASSWORD = Pattern.compile("(?i)(password|passwd|pwd|secret)\\s*[:=]\\s*\\S+");
    private static final Pattern DB_CONNECTION = Pattern.compile("(?i)(jdbc:|mongodb://|mysql://|postgres://|redis://)\\S+");
    private static final Pattern SOURCE_CODE = Pattern.compile("(?i)(private\\s+key|BEGIN RSA|import\\s+os|def\\s+\\w+\\(|function\\s+\\w+\\()");

    @Data
    @AllArgsConstructor
    public static class ClassificationResult
    {
        private final int score;
        private final String verdict;
        private final List<String> reasons;

    }
    private boolean matches(Pattern pattern, String text)
    {
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
    public  ClassificationResult clasify(String text)
    {
        List<String> reasons = new ArrayList<>();

        int score =0;
        if(matches(AADHAAR,text))
        {
            reasons.add("Adhaar number detected");
            score+=40;
        }
        if(matches(CreditCard,text))
        {
            reasons.add("credit card number detected");
            score+=55;
        }
        if(matches(PANCARD,text))
        {
            reasons.add("Pan card number detected");
            score+=40;
        }
        if(matches(API_KEY,text))
        {
            reasons.add("Api key detected");
            score+=80;
        }
        if(matches(EMAIL,text))
        {
            reasons.add("email detected");
            score+=50;
        }
        if(matches(MOBILE,text))
        {
            reasons.add("Mobile number detected");
            score+=50;
        }
        if(matches(UPI,text)){
            reasons.add("Upi id detected");
            score+=60;
        }

        if (matches(PASSWORD, text)) {
            reasons.add("Plain text password detected");
            score += 70;
        }
        if (matches(DB_CONNECTION, text)) {
            reasons.add("Database connection string detected");
            score += 70;
        }
        if (matches(SOURCE_CODE, text)) {
            reasons.add("Source code or private key detected");
            score += 60;
        }
        score=Math.min(score,100);
        String verdict;
        if(score>=50)
        {
            verdict="BLOCKED";
        }
        else if(score>=30)
        {
            verdict="WARNING";
        }
        else {
            verdict="SAFE";
        }
        if(reasons.isEmpty())
        {
            reasons.add("no sensitive data detected");
        }
        return new ClassificationResult(score,verdict,reasons);
    }
}
