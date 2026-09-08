package com.bcafinance.backend_saku.core.storage;

import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path storageRoot;

    public FileStorageService(@Value("${app.file-storage-dir:uploads}") String storageDirectory) {
        this.storageRoot = Paths.get(storageDirectory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new BussinessRuleException("File tidak boleh kosong");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }
        }

        Path targetDirectory = storageRoot.resolve(directory).normalize();
        if (!targetDirectory.startsWith(storageRoot)) {
            throw new BussinessRuleException("Lokasi penyimpanan file tidak valid");
        }

        try {
            Files.createDirectories(targetDirectory);
            Path target = targetDirectory.resolve(UUID.randomUUID() + extension);
            file.transferTo(target);
            return storageRoot.relativize(target).toString().replace('\\', '/');
        } catch (IOException exception) {
            throw new BussinessRuleException("File gagal disimpan");
        }
    }
}
