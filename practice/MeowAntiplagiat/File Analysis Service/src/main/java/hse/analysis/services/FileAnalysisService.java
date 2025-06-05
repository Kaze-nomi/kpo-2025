package hse.analysis.services;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.GatewayTimeout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import hse.analysis.domains.Analysis;
import hse.analysis.repositories.FileRepository;

@Service
@Slf4j
public class FileAnalysisService {

    private final WebClient webClient;

    private final FileRepository fileRepository;

    private Boolean isGenerated;

    @Setter
    private Path storagePath = Path.of("/storage");

    public FileAnalysisService(FileRepository fileRepository, WebClient.Builder webClientBuilder,
            Optional<String> url) {
        this.fileRepository = fileRepository;
        if (url.isEmpty()) {
            this.webClient = webClientBuilder.baseUrl("https://quickchart.io").build();
        } else {
            this.webClient = webClientBuilder.baseUrl(url.get()).build();
        }
    }

    public String analyzeFile(String fileId, String fileName, String fileContent) throws Exception {

        isGenerated = true;

        String error = "";

        var existingAnalysis = fileRepository.findById(Integer.parseInt(fileId)).orElse(null);
        if (existingAnalysis != null) {
            throw new IOException("Анализ файла уже существует");
        }

        int charCount = fileContent.length();
        int wordCount = fileContent.isEmpty() ? 0 : fileContent.split("\\s+").length;
        int paragraphCount = fileContent.isEmpty() ? 0 : fileContent.split("\\r?\\n\\r?\\n").length;

        String location = null;
        try {
            location = generateWordCloud(fileId, fileName, fileContent);
        } catch (Exception e) {
            isGenerated = false;
            if (e.getCause() instanceof GatewayTimeout) {
                error = String.format("Ошибка генерации облака слов: Вероятно либо сервис quickchart.io недоступен, либо из-за большого размера текста, облако слов не может быть сгенерировано: " + e.getMessage(), e);
            } else {
                error = String.format("Ошибка генерации облака слов: " + e.getMessage(), e);
            }
        }

        String result = "Количество символов: " + charCount + "\nКоличество слов: " + wordCount
                + "\nКоличество параграфов: " + paragraphCount;

        Analysis analysis = new Analysis();
        analysis.setFileId(Integer.parseInt(fileId));
        analysis.setResult(result);
        analysis.setCloudLocation(location);

        try {
            fileRepository.save(analysis);
        } catch (Exception e) {
            throw new IOException("Ошибка сохранения анализа: " + e.getMessage(), e);
        }

        result += (isGenerated ? "" : ("\n\n" + error));

        return result;
    }

    public Analysis getFile(String fileId) {
        var existingAnalysis = fileRepository.findById(Integer.parseInt(fileId)).orElse(null);
        if (existingAnalysis != null) {
            return existingAnalysis;
        }
        return null;
    }

    public byte[] getWordCloud(String fileId) throws IOException {
        Analysis analysis = getFile(fileId);

        if (analysis == null) {
            throw new IOException("Для получения облака слов нужно сначала провести анализ файла!");
        }

        if (analysis.getCloudLocation() == null) {
            throw new IOException("Облако слов не было сгенерировано!");
        }

        String cloudPath = analysis.getCloudLocation();

        try {
            return Files.readAllBytes(Path.of(cloudPath));
        } catch (NoSuchFileException e) {
            throw new IOException("По неизвестной причине облако не найдено!");
        }
    }

    private String generateWordCloud(String fileId, String fileName, String fileContent) {
        String imageName = "wordcloud_" + fileName + ".png";
        Path imagePath = storagePath.resolve(imageName);

        String optimizedContent = fileContent
        .replaceAll("\\s+", " ")
        .replaceAll("[^\\p{L}\\p{Nd} ]", "")
        .trim();

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();

            requestBody.put("format", "png")
                    .put("text", optimizedContent);

            String jsonBody = mapper.writeValueAsString(requestBody);

            byte[] imageBytes = webClient.post()
                    .uri("/wordcloud")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            Files.write(imagePath, imageBytes, StandardOpenOption.CREATE_NEW);

            return imagePath.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
