package com.example.tp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private final Path uploadDirectory = Paths.get("uploads", "products").toAbsolutePath().normalize();

    public String storeProductImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Format image non autorise. Utilisez JPG, PNG, WEBP ou GIF.");
        }

        try {
            Files.createDirectories(uploadDirectory);
            String filename = UUID.randomUUID() + "." + extension;
            Path destination = uploadDirectory.resolve(filename).normalize();
            file.transferTo(destination);
            return "/uploads/products/" + filename;
        } catch (IOException ex) {
            throw new IllegalStateException("Impossible d'enregistrer l'image du produit.", ex);
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("Le fichier image doit avoir une extension.");
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
}
