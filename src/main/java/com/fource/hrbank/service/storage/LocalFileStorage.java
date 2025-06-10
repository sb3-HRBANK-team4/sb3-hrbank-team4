package com.fource.hrbank.service.storage;

import com.fource.hrbank.annotation.Logging;
import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.FileIOException;
import com.fource.hrbank.exception.FileNotFoundException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 로컬 디스크에 파일을 저장하는 서비스입니다. (설정 값 'hrbank.storage.type=local'일 때 활성화)
 */
@ConditionalOnProperty(name = "hrbank.storage.type", havingValue = "local")
@Component
@Slf4j
@Logging
public class LocalFileStorage implements FileStorage {

    private final Path root;

    public LocalFileStorage(
        @Value(".hrbank/storage") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    /**
     * 저장 디렉토리를 생성합니다.
     *
     * @throws FileIOException
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new FileIOException(ResponseMessage.FILE_CREATE_ERROR_MESSAGE, ResponseDetails.FILE_CREATE_ERROR_MESSAGE);
        }
    }

    /**
     * ID, 파일 데이터를 받아 로컬에 저장합니다.
     *
     * @param id    저장할 파일의 ID
     * @param bytes 저장할 파일의 데이터
     * @return 저장된 파일의 ID
     * @throws FileIOException
     */
    @Override
    public Long put(Long id, byte[] bytes) {
        Path path = resolvePath(id);

        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        } catch (IOException e) {
            throw new FileIOException(ResponseMessage.FILE_SAVE_ERROR_MESSAGE, ResponseDetails.FILE_SAVE_ERROR_MESSAGE);
        }

        return id;
    }

    /**
     * ID에 해당하는 로컬 파일의 경로를 반환합니다.
     *
     * @param id 파일 ID
     * @return 로컬 파일 경로
     */
    public Path resolvePath(Long id) {
        return root.resolve(id.toString());
    }

    /**
     * ID에 해당하는 파일을 읽어 InputStream으로 반환합니다.
     *
     * @param id 파일 ID
     * @return 파일 입력 스트림
     * @throws FileNotFoundException
     * @throws FileIOException
     */
    @Override
    public InputStream get(Long id) {

        Path path = resolvePath(id);

        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new FileIOException(ResponseMessage.FILE_READ_ERROR_MESSAGE, ResponseDetails.FILE_READ_ERROR_MESSAGE);
        }
    }

    /**
     * 파일 다운로드용 HTTP 응답으로 반환합니다.
     *
     * @param fileMetadata 다운로드 할 파일의 메타정보
     * @return 다운로드용 HTTP 응답 (파일 데이터 포함)
     */
    @Override
    public ResponseEntity<Resource> download(FileMetadata fileMetadata) {
        InputStream stream = get(fileMetadata.getId());

        Resource resource = new InputStreamResource(stream);

        return ResponseEntity.status(HttpStatus.OK)
            .contentLength(fileMetadata.getSize())
            .contentType(MediaType.parseMediaType(fileMetadata.getContentType()))
            .headers(headers -> headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(fileMetadata.getFileName(), StandardCharsets.UTF_8)
                        .build()))
            .body(resource);
    }
}