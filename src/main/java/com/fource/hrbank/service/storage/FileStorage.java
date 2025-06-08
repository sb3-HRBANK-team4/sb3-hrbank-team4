package com.fource.hrbank.service.storage;

import com.fource.hrbank.domain.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;

public interface FileStorage {

    Long put(Long id, byte[] bytes);

    InputStream get(Long id);

    ResponseEntity<Resource> download(FileMetadata fileMetadata);
}
