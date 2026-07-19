package com.sentinel.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileScanningService {

    private final Tika tika = new Tika();

    public String extractText(MultipartFile file)  throws Exception
    {
        return tika.parseToString(file.getInputStream());
    }
}
