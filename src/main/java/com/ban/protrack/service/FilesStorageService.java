package com.ban.protrack.service;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
public interface FilesStorageService {
    public void init();
    public String save(MultipartFile file, String sender);
    public Resource load(String filename);
    public void delete(String filename);
}