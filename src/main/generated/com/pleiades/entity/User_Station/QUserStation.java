package com.pleiades.entity.User_Station;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserStation is a Querydsl query type for UserStation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserStation extends EntityPathBase<UserStation> {

    private static final long serialVersionUID = 1051892182L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserStation userStation = new QUserStation("userStation");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final BooleanPath favorite = createBoolean("favorite");

    public final QUserStationId id;

    public final BooleanPath isAdmin = createBoolean("isAdmin");

    public final NumberPath<Float> positionX = createNumber("positionX", Float.class);

    public final NumberPath<Float> positionY = createNumber("positionY", Float.class);

    public final com.pleiades.entity.QStation station;

    public final BooleanPath todayReport = createBoolean("todayReport");

    public final com.pleiades.entity.QUser user;

    public QUserStation(String variable) {
        this(UserStation.class, forVariable(variable), INITS);
    }

    public QUserStation(Path<? extends UserStation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserStation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserStation(PathMetadata metadata, PathInits inits) {
        this(UserStation.class, metadata, inits);
    }

    public QUserStation(Class<? extends UserStation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QUserStationId(forProperty("id")) : null;
        this.station = inits.isInitialized("station") ? new com.pleiades.entity.QStation(forProperty("station"), inits.get("station")) : null;
        this.user = inits.isInitialized("user") ? new com.pleiades.entity.QUser(forProperty("user")) : null;
    }

}

