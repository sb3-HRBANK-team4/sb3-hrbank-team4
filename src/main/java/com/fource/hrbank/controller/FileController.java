package com.fource.hrbank.controller;

import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.exception.FileNotFoundException;
import com.fource.hrbank.repository.FileMetadataRepository;
import com.fource.hrbank.service.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileStorage fileStorage;
    private final FileMetadataRepository fileMetadataRepository;

    @GetMapping(path = "/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {

        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
            .orElseThrow(FileNotFoundException::new);

        return fileStorage.download(fileMetadata);
    }
}
