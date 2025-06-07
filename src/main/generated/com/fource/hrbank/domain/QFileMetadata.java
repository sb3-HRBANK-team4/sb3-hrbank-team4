package com.fource.hrbank.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileMetadata is a Querydsl query type for FileMetadata
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileMetadata extends EntityPathBase<FileMetadata> {

    private static final long serialVersionUID = -911131994L;

    public static final QFileMetadata fileMetadata = new QFileMetadata("fileMetadata");

    public final com.fource.hrbank.domain.common.QBaseEntity _super = new com.fource.hrbank.domain.common.QBaseEntity(this);

    public final StringPath contentType = createString("contentType");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath fileName = createString("fileName");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Long> size = createNumber("size", Long.class);

    public QFileMetadata(String variable) {
        super(FileMetadata.class, forVariable(variable));
    }

    public QFileMetadata(Path<? extends FileMetadata> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileMetadata(PathMetadata metadata) {
        super(FileMetadata.class, metadata);
    }

}

