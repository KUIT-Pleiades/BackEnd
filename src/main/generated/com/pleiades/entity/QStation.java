package com.pleiades.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStation is a Querydsl query type for Station
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStation extends EntityPathBase<Station> {

    private static final long serialVersionUID = 1159478393L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStation station = new QStation("station");

    public final StringPath adminUserId = createString("adminUserId");

    public final com.pleiades.entity.character.QTheItem background;

    public final QUser backgroundOwner;

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath intro = createString("intro");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> numberOfUsers = createNumber("numberOfUsers", Integer.class);

    public final ComparablePath<java.util.UUID> publicId = createComparable("publicId", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> recentActivity = createDateTime("recentActivity", java.time.LocalDateTime.class);

    public final TimePath<java.time.LocalTime> reportNoticeTime = createTime("reportNoticeTime", java.time.LocalTime.class);

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QStation(String variable) {
        this(Station.class, forVariable(variable), INITS);
    }

    public QStation(Path<? extends Station> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStation(PathMetadata metadata, PathInits inits) {
        this(Station.class, metadata, inits);
    }

    public QStation(Class<? extends Station> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.background = inits.isInitialized("background") ? new com.pleiades.entity.character.QTheItem(forProperty("background")) : null;
        this.backgroundOwner = inits.isInitialized("backgroundOwner") ? new QUser(forProperty("backgroundOwner")) : null;
    }

}

