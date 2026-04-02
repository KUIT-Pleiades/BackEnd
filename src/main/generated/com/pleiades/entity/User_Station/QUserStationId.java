package com.pleiades.entity.User_Station;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserStationId is a Querydsl query type for UserStationId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserStationId extends BeanPath<UserStationId> {

    private static final long serialVersionUID = 1551074705L;

    public static final QUserStationId userStationId = new QUserStationId("userStationId");

    public final NumberPath<Long> stationId = createNumber("stationId", Long.class);

    public final StringPath userId = createString("userId");

    public QUserStationId(String variable) {
        super(UserStationId.class, forVariable(variable));
    }

    public QUserStationId(Path<? extends UserStationId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserStationId(PathMetadata metadata) {
        super(UserStationId.class, metadata);
    }

}

