package com.pleiades.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStationQuestion is a Querydsl query type for StationQuestion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStationQuestion extends EntityPathBase<StationQuestion> {

    private static final long serialVersionUID = 1171608447L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStationQuestion stationQuestion = new QStationQuestion("stationQuestion");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QQuestion question;

    public final QStation station;

    public QStationQuestion(String variable) {
        this(StationQuestion.class, forVariable(variable), INITS);
    }

    public QStationQuestion(Path<? extends StationQuestion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStationQuestion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStationQuestion(PathMetadata metadata, PathInits inits) {
        this(StationQuestion.class, metadata, inits);
    }

    public QStationQuestion(Class<? extends StationQuestion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new QQuestion(forProperty("question")) : null;
        this.station = inits.isInitialized("station") ? new QStation(forProperty("station"), inits.get("station")) : null;
    }

}

