package com.pleiades.entity.store.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemKeyword is a Querydsl query type for ItemKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemKeyword extends EntityPathBase<ItemKeyword> {

    private static final long serialVersionUID = -190010024L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemKeyword itemKeyword = new QItemKeyword("itemKeyword");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pleiades.entity.character.QTheItem item;

    public final QKeyword keyword;

    public QItemKeyword(String variable) {
        this(ItemKeyword.class, forVariable(variable), INITS);
    }

    public QItemKeyword(Path<? extends ItemKeyword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemKeyword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemKeyword(PathMetadata metadata, PathInits inits) {
        this(ItemKeyword.class, metadata, inits);
    }

    public QItemKeyword(Class<? extends ItemKeyword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.pleiades.entity.character.QTheItem(forProperty("item")) : null;
        this.keyword = inits.isInitialized("keyword") ? new QKeyword(forProperty("keyword")) : null;
    }

}

