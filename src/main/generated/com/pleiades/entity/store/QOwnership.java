package com.pleiades.entity.store;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOwnership is a Querydsl query type for Ownership
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOwnership extends EntityPathBase<Ownership> {

    private static final long serialVersionUID = 664416487L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOwnership ownership = new QOwnership("ownership");

    public final BooleanPath active = createBoolean("active");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pleiades.entity.character.QTheItem item;

    public final DateTimePath<java.time.LocalDateTime> purchasedAt = createDateTime("purchasedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> purchasedPrice = createNumber("purchasedPrice", Long.class);

    public final EnumPath<com.pleiades.strings.ItemSource> source = createEnum("source", com.pleiades.strings.ItemSource.class);

    public final com.pleiades.entity.QUser user;

    public QOwnership(String variable) {
        this(Ownership.class, forVariable(variable), INITS);
    }

    public QOwnership(Path<? extends Ownership> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOwnership(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOwnership(PathMetadata metadata, PathInits inits) {
        this(Ownership.class, metadata, inits);
    }

    public QOwnership(Class<? extends Ownership> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.pleiades.entity.character.QTheItem(forProperty("item")) : null;
        this.user = inits.isInitialized("user") ? new com.pleiades.entity.QUser(forProperty("user")) : null;
    }

}

