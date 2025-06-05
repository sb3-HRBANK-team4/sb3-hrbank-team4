package com.fource.hrbank.service.backup.storage;

import com.fource.hrbank.exception.FileIOException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 로컬 디스크에 파일을 저장하는 서비스입니다.
 * (설정 값 'hrbank.storage.type=local'일 때 활성화)
 */
@ConditionalOnProperty(name = "hrbank.storage.type", havingValue = "local")
@Component
public class LocalFileStorage implements FileStorage {

    private final Path root;

    public LocalFileStorage(
            @Value(".hrbank/storage") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    /**
     * 저장 디렉토리를 생성합니다.
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new FileIOException();
        }
    }

    /**
     * ID, 파일 데이터를 받아 로컬에 저장합니다.
     *
     * @param id 저장할 파일의 ID
     * @param bytes 저장할 파일의 데이터
     * @return 저장된 파일의 ID
     */
    @Override
    public Long put(Long id, byte[] bytes) {
        Path path = resolvePath(id);

        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        } catch (IOException e) {
            throw new FileIOException(FileIOException.FILE_SAVE_ERROR_MESSAGE, e);
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
}
