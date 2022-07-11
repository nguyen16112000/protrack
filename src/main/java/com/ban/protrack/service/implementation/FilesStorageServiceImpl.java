package com.ban.protrack.service.implementation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.ban.protrack.service.FilesStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
    private final Path root = Paths.get("uploads");
    @Override
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }
    @Override
    public String save(MultipartFile file, String sender) {
        try {
            String fileName = sender + "_" + generateRandomString();
            String mime_type = file.getContentType();
//            System.out.println(mime_type);
            String[] strings = file.getOriginalFilename().split("\\.");
            String fileType = strings[strings.length - 1];
            List<String> allowed_mime_type = Arrays.asList(
                    "text/csv",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "image/jpeg",
                    "application/json",
                    "text/plain",
                    "image/png",
                    "application/pdf",
                    "application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.rar",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/zip",
                    "application/x-7z-compressed"
            );

            if (allowed_mime_type.contains(mime_type)) {
                Files.copy(file.getInputStream(), this.root.resolve(fileName + "." + fileType));
                return fileName + "." + fileType;
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not upload the file: " + file.getOriginalFilename() + "!");
        }
        throw new RuntimeException("File type is not supported");
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = root.resolve(filename);
            Files.delete(file);
        }
        catch (Exception ex){
            throw new RuntimeException("Could not delete the file!");
        }
    }

    private String generateRandomString() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 20;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

//    private String setProofUrl(String username, String type) {
//        String fileLocation = generateRandomString();
//        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/file/" + username + "_" + fileLocation + "." + type).toUriString();
//    }
}