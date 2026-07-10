package com.sentinel.controller;

import com.sentinel.model.ScanRequest;
import com.sentinel.model.ScanResult;
import com.sentinel.service.DataClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.crypto.Data;

public class scancontroller
{
    @Autowired
    private DataClassifier classifier;


    @PostMapping("/scan")
    public ScanResult scan(@RequestBody ScanRequest request)
    {
        DataClassifier.ClassificationResult result = classifier.clasify(request.getText());
        if(!result.getVerdict().equals("SAFE"))
        {

        }
        return new ScanResult();
    }

}
