package com.sentinel.controller;

import com.sentinel.model.Alert;
import com.sentinel.model.ScanRequest;
import com.sentinel.model.ScanResult;
import com.sentinel.service.AlertRepository;
import com.sentinel.service.DataClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class scancontroller
{
    @Autowired
    private DataClassifier classifier;

    @Autowired
    AlertRepository alertRepository;

    @PostMapping("/scan")
    public ScanResult scan(@RequestBody ScanRequest request)
    {
        DataClassifier.ClassificationResult result = classifier.clasify(request.getText());
        String text = request.getText();
        if(!result.getVerdict().equals("SAFE"))
        {
            Alert alert = new Alert(text,result.getScore(),result.getVerdict(),result.getReasons());
            alertRepository.save(alert);
        }
        return new ScanResult(text,result.getScore(),result.getVerdict(),result.getReasons());
    }

    @GetMapping("/alerts")
    public List<Alert> getAlerts(){
        return alertRepository.findAllByOrderByIdDesc();
    }

}
