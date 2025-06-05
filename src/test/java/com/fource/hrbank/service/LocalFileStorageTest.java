package com.fource.hrbank.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fource.hrbank.service.backup.storage.FileStorage;
import com.fource.hrbank.service.backup.storage.LocalFileStorage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LocalFileStorageTest {

    @Autowired
    private LocalFileStorage fileStorage;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorage = new LocalFileStorage(tempDir.toString());
        fileStorage.init();
    }

    @Test
    void put_정상동작_파일저장됨() throws IOException {

        // given
        Long id = 1L;
        byte[] content = "테스트".getBytes();

        // when
        Long savedId = fileStorage.put(id, content);

        // then
        Path savedPath = fileStorage.resolvePath(savedId);
        assertThat(savedId).isEqualTo(id);
        assertThat(Files.exists(savedPath)).isTrue();
        assertThat(Files.readAllBytes(savedPath)).isEqualTo(content);
    }
}
