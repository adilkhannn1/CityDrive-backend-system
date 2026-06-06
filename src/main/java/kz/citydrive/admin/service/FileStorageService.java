package kz.citydrive.admin.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    public String saveCompanyDocument(Long userId, String fieldName, MultipartFile file) {
        validateCompanyDocument(file);

        String ext = extensionFor(file);
        if (".pdf".equals(ext)) {
            validatePdfContent(file);
        }
        String filename = fieldName + ext;
        Path targetDir = Paths.get(uploadsDir, "companies", String.valueOf(userId)).toAbsolutePath().normalize();

        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save file");
        }

        return "/uploads/companies/" + userId + "/" + filename;
    }

    public void deleteCompanyDirectory(Long userId) {
        Path dir = Paths.get(uploadsDir, "companies", String.valueOf(userId)).toAbsolutePath().normalize();
        if (!Files.exists(dir)) {
            return;
        }
        try {
            Files.walk(dir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                            // best effort cleanup
                        }
                    });
        } catch (IOException ignored) {
            // best effort cleanup
        }
    }

    public String resolvePublicUrl(String storedPath, HttpServletRequest request) {
        if (storedPath == null || storedPath.isBlank()) {
            return null;
        }
        if (storedPath.startsWith("http")) {
            return storedPath;
        }
        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return storedPath.startsWith("/") ? base + storedPath : base + "/" + storedPath;
    }

    public Resource loadStoredFile(String storedPath) {
        Path filePath = resolveLocalPath(storedPath);
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }
            return resource;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
    }

    public MediaType mediaTypeFor(String storedPath) {
        String lower = storedPath.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        }
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    public String downloadFilename(String storedPath, String fallbackName) {
        if (storedPath == null || storedPath.isBlank()) {
            return fallbackName;
        }
        int slash = storedPath.lastIndexOf('/');
        if (slash >= 0 && slash < storedPath.length() - 1) {
            return storedPath.substring(slash + 1);
        }
        return fallbackName;
    }

    private Path resolveLocalPath(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        String relative = storedPath.trim();
        if (relative.startsWith("http")) {
            int uploadsIndex = relative.indexOf("/uploads/");
            if (uploadsIndex < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }
            relative = relative.substring(uploadsIndex);
        }

        if (!relative.startsWith("/uploads/")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        String underUploads = relative.substring("/uploads/".length());
        Path filePath = Paths.get(uploadsDir, underUploads.split("/")).toAbsolutePath().normalize();
        Path uploadsRoot = Paths.get(uploadsDir).toAbsolutePath().normalize();
        if (!filePath.startsWith(uploadsRoot)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        return filePath;
    }

    private void validateCompanyDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size must not exceed 10 MB");
        }

        String ext = extensionFor(file);
        if (!ALLOWED_EXTENSIONS.contains(ext.substring(1))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Allowed formats: pdf, jpg, jpeg, png");
        }
    }

    private String extensionFor(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            String ext = original.substring(original.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(ext.substring(1))) {
                return ext;
            }
        }

        String contentType = file.getContentType();
        if (contentType != null) {
            return switch (contentType.toLowerCase(Locale.ROOT)) {
                case "application/pdf" -> ".pdf";
                case "image/png" -> ".png";
                case "image/jpeg", "image/jpg" -> ".jpg";
                default -> throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Allowed formats: pdf, jpg, jpeg, png");
            };
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to detect file format");
    }

    private void validatePdfContent(MultipartFile file) {
        try {
            byte[] header = file.getInputStream().readNBytes(5);
            if (header.length < 4 || !new String(header, 0, 4, StandardCharsets.US_ASCII).equals("%PDF")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid PDF file");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid PDF file");
        }
    }
}
