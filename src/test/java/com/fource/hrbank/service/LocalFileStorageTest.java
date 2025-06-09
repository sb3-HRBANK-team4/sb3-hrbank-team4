package com.fource.hrbank.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.fource.hrbank.domain.FileMetadata;
import com.fource.hrbank.dto.common.ResponseDetails;
import com.fource.hrbank.dto.common.ResponseMessage;
import com.fource.hrbank.exception.FileIOException;
import com.fource.hrbank.exception.FileNotFoundException;
import com.fource.hrbank.repository.FileMetadataRepository;
import com.fource.hrbank.service.storage.LocalFileStorage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LocalFileStorageTest {

    @Autowired
    private LocalFileStorage fileStorage;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorage = new LocalFileStorage(tempDir.toString());
        fileStorage.init();

        // 테이블 ID 시퀀스 초기화
        jdbcTemplate.execute("TRUNCATE TABLE tbl_file_metadata RESTART IDENTITY CASCADE");
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
            .hasMessage(ResponseMessage.FILE_SAVE_ERROR_MESSAGE, ResponseDetails.FILE_SAVE_ERROR_MESSAGE);
    }

    @Test
    void get_파일읽기성공_파일읽음() throws IOException {
        // given
        Long id = 1L;
        byte[] content = "파일 읽기 테스트".getBytes();

        Path filePath = fileStorage.resolvePath(id);
        Files.write(filePath, content);

        // when
        InputStream inputStream = fileStorage.get(id);
        byte[] results = inputStream.readAllBytes();

        // then
        assertThat(results).isEqualTo(content);
    }

    @Test
    void get_파일읽기실패_예외발생() {
        // given
        Long id = 999L; // 존재하지 않는 파일 ID

        // when
        assertThatThrownBy(() -> fileStorage.get(id))
            .isInstanceOf(FileNotFoundException.class)
            .hasMessage(ResponseDetails.FILE_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    void download_파일다운로드성공_다운로드됨() throws IOException {
        // given
        byte[] content = "파일 다운로드 테스트".getBytes();

        FileMetadata fileMetadata = new FileMetadata("download.txt", "text/plain",
            (long) content.length);
        fileMetadataRepository.save(fileMetadata);

        Path downloadPath = fileStorage.resolvePath(fileMetadata.getId());
        Files.write(downloadPath, content);

        // when
        ResponseEntity<Resource> response = fileStorage.download(fileMetadata);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_PLAIN);
        assertThat(response.getHeaders().getContentLength()).isEqualTo(content.length);
        assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo(
            "download.txt");
        assertThat(Files.readAllBytes(downloadPath)).isEqualTo(content);
    }
}
