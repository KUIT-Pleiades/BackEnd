package com.pleiades.entity.store.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemColor is a Querydsl query type for ItemColor
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemColor extends EntityPathBase<ItemColor> {

    private static final long serialVersionUID = 2030685842L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemColor itemColor = new QItemColor("itemColor");

    public final QColor color;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pleiades.entity.character.QTheItem item;

    public QItemColor(String variable) {
        this(ItemColor.class, forVariable(variable), INITS);
    }

    public QItemColor(Path<? extends ItemColor> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemColor(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemColor(PathMetadata metadata, PathInits inits) {
        this(ItemColor.class, metadata, inits);
    }

    public QItemColor(Class<? extends ItemColor> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.color = inits.isInitialized("color") ? new QColor(forProperty("color")) : null;
        this.item = inits.isInitialized("item") ? new com.pleiades.entity.character.QTheItem(forProperty("item")) : null;
    }

}

