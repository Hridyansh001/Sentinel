package com.sentinel.service;

import org.springframework.stereotype.Service;

@Service
public class Masking {
    public String maskText(String text)
    {
        text = text.replaceAll("\\b(sk-|ghp_|AIza|Bearer\\s)[A-Za-z0-9_\\-]{20,}\\b" , "$1************"); //api

        text = text.replaceAll("\\b[0-9]{4}\\s?[0-9]{4}\\s?[0-9]{4}\\b", "******");  //aadhaar

        text = text.replaceAll("\\b(?:\\d[ -]?){13,16}\\b", "**** **** ****");   //credit card

        text=text.replaceAll("\\b[A-Z]{5}[0-9]{4}[A-Z]{1}\\b","*********"); //pan

        text=text.replaceAll("\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b","*********@gmail.com");  //mail

        text=text.replaceAll("\\b[6-9][0-9]{9}\\b","*******");

        text=text.replaceAll("\\b[a-zA-Z0-9.\\-_]{2,256}@(okaxis|okhdfcbank|okicici|oksbi|ybl|ibl|axl|waicici|paytm|apl|upi)\\b","********");

        text=text.replaceAll("(?i)(password|passwd|pwd|secret)\\s*[:=]\\s*\\S+","*****");//password

        text=text.replaceAll("(?i)(jdbc:|mongodb://|mysql://|postgres://|redis://)\\S+","******"); //db concection

        text=text.replaceAll("(?i)(private\\s+key|BEGIN RSA|import\\s+os|def\\s+\\w+\\(|function\\s+\\w+\\()","*****"); //source code

        return text;
    }
}
