package com.fource.hrbank.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChangeDetail is a Querydsl query type for ChangeDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChangeDetail extends EntityPathBase<ChangeDetail> {

    private static final long serialVersionUID = 271398396L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChangeDetail changeDetail = new QChangeDetail("changeDetail");

    public final com.fource.hrbank.domain.common.QBaseEntity _super = new com.fource.hrbank.domain.common.QBaseEntity(this);

    public final QChangeLog changeLog;

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath fieldName = createString("fieldName");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath newValue = createString("newValue");

    public final StringPath oldValue = createString("oldValue");

    public QChangeDetail(String variable) {
        this(ChangeDetail.class, forVariable(variable), INITS);
    }

    public QChangeDetail(Path<? extends ChangeDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChangeDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChangeDetail(PathMetadata metadata, PathInits inits) {
        this(ChangeDetail.class, metadata, inits);
    }

    public QChangeDetail(Class<? extends ChangeDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.changeLog = inits.isInitialized("changeLog") ? new QChangeLog(forProperty("changeLog"), inits.get("changeLog")) : null;
    }

}

