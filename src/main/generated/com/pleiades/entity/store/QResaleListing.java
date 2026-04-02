package com.pleiades.entity.store;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QResaleListing is a Querydsl query type for ResaleListing
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResaleListing extends EntityPathBase<ResaleListing> {

    private static final long serialVersionUID = -480403038L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResaleListing resaleListing = new QResaleListing("resaleListing");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final QOwnership resultOwnership;

    public final QOwnership sourceOwnership;

    public final EnumPath<com.pleiades.strings.SaleStatus> status = createEnum("status", com.pleiades.strings.SaleStatus.class);

    public QResaleListing(String variable) {
        this(ResaleListing.class, forVariable(variable), INITS);
    }

    public QResaleListing(Path<? extends ResaleListing> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QResaleListing(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QResaleListing(PathMetadata metadata, PathInits inits) {
        this(ResaleListing.class, metadata, inits);
    }

    public QResaleListing(Class<? extends ResaleListing> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.resultOwnership = inits.isInitialized("resultOwnership") ? new QOwnership(forProperty("resultOwnership"), inits.get("resultOwnership")) : null;
        this.sourceOwnership = inits.isInitialized("sourceOwnership") ? new QOwnership(forProperty("sourceOwnership"), inits.get("sourceOwnership")) : null;
    }

}

