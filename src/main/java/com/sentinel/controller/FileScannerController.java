package com.sentinel.controller;

import com.sentinel.model.Alert;
import com.sentinel.model.ScanResult;
import com.sentinel.service.AlertRepository;
import com.sentinel.service.DataClassifier;
import com.sentinel.service.FileScanningService;
import com.sentinel.service.Masking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileScannerController {
    @Autowired
    DataClassifier dataClassifier;
    @Autowired
    Masking masking;
    @Autowired
    AlertRepository alertRepository;
    @Autowired
    FileScanningService fileScanningService;
    @PostMapping("/scan/file")
    public ScanResult scanFile(@RequestParam("file")MultipartFile file) throws Exception
    {
        if(file.isEmpty())
        {
            throw new RuntimeException("No file Detected");
        }
        String text = fileScanningService.extractText(file);
        DataClassifier.ClassificationResult classificationResult = dataClassifier.clasify(text);
        if(!classificationResult.getVerdict().equals("SAFE"))
        {
            Alert alert = new Alert(masking.maskText(text), classificationResult.getScore(), classificationResult.getVerdict(), classificationResult.getReasons());
            alertRepository.save(alert);
        }
        return new ScanResult(
                masking.maskText(text),
                classificationResult.getScore(),
                classificationResult.getVerdict(),
                classificationResult.getReasons()
        );
    }
}
