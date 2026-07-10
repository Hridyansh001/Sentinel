package com.sentinel.controller;

import com.sentinel.model.ScanRequest;
import com.sentinel.model.ScanResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class scancontroller
{
    @PostMapping("/scan")
    public ScanResult scan(@RequestBody ScanRequest request)
    {

        return new ScanResult();
    }

}
