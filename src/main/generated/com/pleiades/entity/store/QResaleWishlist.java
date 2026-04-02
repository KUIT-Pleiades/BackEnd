package com.pleiades.entity.store;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QResaleWishlist is a Querydsl query type for ResaleWishlist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResaleWishlist extends EntityPathBase<ResaleWishlist> {

    private static final long serialVersionUID = -27544985L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResaleWishlist resaleWishlist = new QResaleWishlist("resaleWishlist");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QResaleListing resaleListing;

    public final com.pleiades.entity.QUser user;

    public QResaleWishlist(String variable) {
        this(ResaleWishlist.class, forVariable(variable), INITS);
    }

    public QResaleWishlist(Path<? extends ResaleWishlist> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QResaleWishlist(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QResaleWishlist(PathMetadata metadata, PathInits inits) {
        this(ResaleWishlist.class, metadata, inits);
    }

    public QResaleWishlist(Class<? extends ResaleWishlist> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.resaleListing = inits.isInitialized("resaleListing") ? new QResaleListing(forProperty("resaleListing"), inits.get("resaleListing")) : null;
        this.user = inits.isInitialized("user") ? new com.pleiades.entity.QUser(forProperty("user")) : null;
    }

}

