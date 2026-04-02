package com.pleiades.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReportHistory is a Querydsl query type for ReportHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportHistory extends EntityPathBase<ReportHistory> {

    private static final long serialVersionUID = -1506437051L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReportHistory reportHistory = new QReportHistory("reportHistory");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath query = createString("query");

    public final QUser user;

    public QReportHistory(String variable) {
        this(ReportHistory.class, forVariable(variable), INITS);
    }

    public QReportHistory(Path<? extends ReportHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReportHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReportHistory(PathMetadata metadata, PathInits inits) {
        this(ReportHistory.class, metadata, inits);
    }

    public QReportHistory(Class<? extends ReportHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

