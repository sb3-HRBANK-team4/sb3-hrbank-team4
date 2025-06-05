package com.fource.hrbank.service.backup.storage;

import java.io.IOException;

public interface FileStorage {

    Long put(Long id, byte[] bytes) throws IOException;
}
