package com.sentinel.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EntropyCalculator {
    public double calculate(String text)
    {
        if(text == null || text.isEmpty()) return 0;
        Map<Character,Integer> frequency = new HashMap<>();
        for(char c: text.toCharArray())
        {
            frequency.put(c,frequency.getOrDefault(c,0)+1);
        }
        double entropy=0;
        int length =text.length();
        for(int count :frequency.values())
        {
            double probability = (double) count /length;
            entropy-= probability*(Math.log(probability))/Math.log(2);
        }
        return entropy;
    }
    public boolean ishighEntropy(String text)
    {
        return text.length()>30&& calculate(text)>4.0;
    }
}
