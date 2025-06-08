package com.fource.hrbank.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChangeLog is a Querydsl query type for ChangeLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChangeLog extends EntityPathBase<ChangeLog> {

    private static final long serialVersionUID = -106668679L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChangeLog changeLog = new QChangeLog("changeLog");

    public final com.fource.hrbank.domain.common.QBaseEntity _super = new com.fource.hrbank.domain.common.QBaseEntity(this);

    public final DateTimePath<java.time.Instant> changedAt = createDateTime("changedAt", java.time.Instant.class);

    public final ListPath<ChangeDetail, QChangeDetail> changeDetailList = this.<ChangeDetail, QChangeDetail>createList("changeDetailList", ChangeDetail.class, QChangeDetail.class, PathInits.DIRECT2);

    public final StringPath changedIp = createString("changedIp");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final QEmployee employee;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath memo = createString("memo");

    public final EnumPath<ChangeType> type = createEnum("type", ChangeType.class);

    public QChangeLog(String variable) {
        this(ChangeLog.class, forVariable(variable), INITS);
    }

    public QChangeLog(Path<? extends ChangeLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChangeLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChangeLog(PathMetadata metadata, PathInits inits) {
        this(ChangeLog.class, metadata, inits);
    }

    public QChangeLog(Class<? extends ChangeLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new QEmployee(forProperty("employee"), inits.get("employee")) : null;
    }

}

