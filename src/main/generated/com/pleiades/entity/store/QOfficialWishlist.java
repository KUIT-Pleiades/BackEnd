package com.pleiades.entity.store;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOfficialWishlist is a Querydsl query type for OfficialWishlist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOfficialWishlist extends EntityPathBase<OfficialWishlist> {

    private static final long serialVersionUID = -1051128488L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOfficialWishlist officialWishlist = new QOfficialWishlist("officialWishlist");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pleiades.entity.character.QTheItem item;

    public final com.pleiades.entity.QUser user;

    public QOfficialWishlist(String variable) {
        this(OfficialWishlist.class, forVariable(variable), INITS);
    }

    public QOfficialWishlist(Path<? extends OfficialWishlist> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOfficialWishlist(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOfficialWishlist(PathMetadata metadata, PathInits inits) {
        this(OfficialWishlist.class, metadata, inits);
    }

    public QOfficialWishlist(Class<? extends OfficialWishlist> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.pleiades.entity.character.QTheItem(forProperty("item")) : null;
        this.user = inits.isInitialized("user") ? new com.pleiades.entity.QUser(forProperty("user")) : null;
    }

}

