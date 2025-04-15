package com.kitty.blog.infrastructure.utils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class TesseractOCR {

    @Autowired
    private Tesseract tesseract;

    public String doOCR(File imageFile) throws TesseractException {
        return tesseract.doOCR(imageFile);
    }

    public String doOCR(BufferedImage image) throws TesseractException {
        return tesseract.doOCR(image);
    }

    public void setLanguage(String language) {
        tesseract.setLanguage(language);
    }

    public void setDatapath(String datapath) {
        tesseract.setDatapath(datapath);
    }
}