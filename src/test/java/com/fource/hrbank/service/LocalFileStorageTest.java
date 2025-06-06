package com.fource.hrbank.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.fource.hrbank.exception.FileIOException;
import com.fource.hrbank.service.backup.storage.LocalFileStorage;
import java.io.File;
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
    void init_디렉토리생성성공_디렉토리생성됨() {
        // given
        Path savePath = tempDir.resolve("save");    // 임시 경로
        LocalFileStorage fileStorage = new LocalFileStorage(savePath.toString());

        // when
        fileStorage.init();

        // then
        File dir = savePath.toFile();
        assertThat(dir.exists()).isTrue();      // 경로 존재 확인
        assertThat(dir.isDirectory()).isTrue(); // 디렉토리인지 확인
    }

    @Test
    void init_디렉토리생성실패_예외발생() throws IOException {
        // given
        Path conflictPath = tempDir.resolve("conflict");
        Files.createFile(conflictPath); // 디렉토리 대신 파일 생성

        LocalFileStorage faultyStorage = new LocalFileStorage(conflictPath.toString());

        // when & then
        assertThatThrownBy(faultyStorage::init)
                .isInstanceOf(FileIOException.class);
    }

    @Test
    void put_파일저장성공_파일저장됨() throws IOException {
        // given
        Long id = 1L;
        byte[] content = "저장 성공 테스트".getBytes();

        // when
        Long savedId = fileStorage.put(id, content);

        // then
        Path savedPath = fileStorage.resolvePath(savedId);
        assertThat(savedId).isEqualTo(id);
        assertThat(Files.exists(savedPath)).isTrue();
        assertThat(Files.readAllBytes(savedPath)).isEqualTo(content);
    }

    @Test
    void put_파일저장실패_예외발생() throws IOException {
        // given
        Long id = 1L;
        byte[] content = "저장 실패 테스트".getBytes();

        Path confilictPath = tempDir.resolve(id.toString());    // 파일 저장 위치에 디렉토리를 생성해 예외 유도
        Files.createDirectory(confilictPath);

        LocalFileStorage faultyStorage = new LocalFileStorage(tempDir.toString());
        faultyStorage.init();

        // when & then
        assertThatThrownBy(() -> fileStorage.put(id, content))
                .isInstanceOf(FileIOException.class)
                .hasMessage(FileIOException.FILE_SAVE_ERROR_MESSAGE);
    }
}
