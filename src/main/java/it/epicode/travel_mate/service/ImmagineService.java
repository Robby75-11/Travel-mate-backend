package it.epicode.travel_mate.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service // Questo è il servizio dedicato al caricamento
public class ImmagineService {

    @Autowired
    private Cloudinary cloudinary;

    public String caricaImmagine(MultipartFile file) throws IOException {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new IOException("Errore durante il caricamento dell'immagine su Cloudinary", e);
        }
    }
}