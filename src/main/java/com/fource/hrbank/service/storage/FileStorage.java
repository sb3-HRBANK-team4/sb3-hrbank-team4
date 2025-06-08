package com.fource.hrbank.service.storage;

import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.employee.EmployeeDto;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface FileStorage {

    Long put(Long id, byte[] bytes);

    InputStream get(Long id);

    ResponseEntity<Resource> download(FileMetadata fileMetadata);
}
