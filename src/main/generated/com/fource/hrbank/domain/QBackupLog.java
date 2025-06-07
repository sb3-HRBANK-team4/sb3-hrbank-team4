package com.fource.hrbank.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBackupLog is a Querydsl query type for BackupLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBackupLog extends EntityPathBase<BackupLog> {

    private static final long serialVersionUID = -206575065L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBackupLog backupLog = new QBackupLog("backupLog");

    public final com.fource.hrbank.domain.common.QBaseEntity _super = new com.fource.hrbank.domain.common.QBaseEntity(this);

    public final QFileMetadata backupFile;

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final DateTimePath<java.time.Instant> endedAt = createDateTime("endedAt", java.time.Instant.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DateTimePath<java.time.Instant> startedAt = createDateTime("startedAt", java.time.Instant.class);

    public final EnumPath<BackupStatus> status = createEnum("status", BackupStatus.class);

    public final StringPath worker = createString("worker");

    public QBackupLog(String variable) {
        this(BackupLog.class, forVariable(variable), INITS);
    }

    public QBackupLog(Path<? extends BackupLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBackupLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBackupLog(PathMetadata metadata, PathInits inits) {
        this(BackupLog.class, metadata, inits);
    }

    public QBackupLog(Class<? extends BackupLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.backupFile = inits.isInitialized("backupFile") ? new QFileMetadata(forProperty("backupFile")) : null;
    }

}

