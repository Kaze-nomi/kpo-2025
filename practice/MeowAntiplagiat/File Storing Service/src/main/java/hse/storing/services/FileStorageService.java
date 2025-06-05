package hse.storing.services;

import hse.storing.domains.File;
import hse.storing.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    
    private final FileRepository fileRepository;
    private final Path storagePath = Path.of("/storage");

    @Transactional
    public String saveFile(String fileName, byte[] content) throws IOException {

        if (content == null || content.length == 0) {
            throw new IOException("Пустой файл");
        }

        if (fileRepository.findByHash(Arrays.hashCode(content)).isPresent()) {
            return "Попался, обманщик! Антиплагиат не обмануть! (почти) Такой файл уже был загружен: ID " + String.valueOf(fileRepository.findByHash(Arrays.hashCode(content)).get().getId());
        }

        if (Files.exists(storagePath.resolve(fileName))) {
            fileName = String.format("%s_SameNameAnotherContent_%d", fileName, System.currentTimeMillis());
        }

        Path filePath = storagePath.resolve(fileName);
        Files.write(filePath, content, StandardOpenOption.CREATE_NEW);
        
        File file = new File();
        file.setName(fileName);
        file.setHash(Arrays.hashCode(content));
        file.setLocation(filePath.toString());
        
        return "ID файла: " + String.valueOf(fileRepository.save(file).getId());
    }

    public String getFileName(int fileId) throws IOException {
        File file = fileRepository.findById(fileId)
            .orElseThrow(() -> new IOException("Файл не найден"));
        return file.getName();
    }
    
    public byte[] getFileContent(int fileId) throws IOException {
        File file = fileRepository.findById(fileId)
            .orElseThrow(() -> new IOException("Файл не найден"));

        Path filePath = Path.of(file.getLocation());
        return Files.readAllBytes(filePath);
    }

}