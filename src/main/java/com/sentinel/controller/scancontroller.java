package com.sentinel.controller;

import com.sentinel.model.Alert;
import com.sentinel.model.ScanRequest;
import com.sentinel.model.ScanResult;
import com.sentinel.service.AlertRepository;
import com.sentinel.service.DataClassifier;
import com.sentinel.service.Masking;
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

    @Autowired
    Masking masking;

    @PostMapping("/scan")
    public ScanResult scan(@RequestBody ScanRequest request)
    {
        DataClassifier.ClassificationResult result = classifier.clasify(request.getText());
        String text = request.getText();
        String maskedText = masking.maskText(text);
        if(!result.getVerdict().equals("SAFE"))
        {
            Alert alert = new Alert(maskedText,result.getScore(),result.getVerdict(),result.getReasons());
            alertRepository.save(alert);
        }
        return new ScanResult(maskedText,result.getScore(),result.getVerdict(),result.getReasons());
    }

    @GetMapping("/alerts")
    public List<Alert> getAlerts(){
        return alertRepository.findAllByOrderByIdDesc();
    }

}
