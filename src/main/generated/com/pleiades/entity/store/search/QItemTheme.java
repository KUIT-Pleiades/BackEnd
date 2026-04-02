package com.pleiades.entity.store.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemTheme is a Querydsl query type for ItemTheme
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemTheme extends EntityPathBase<ItemTheme> {

    private static final long serialVersionUID = 2046170360L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemTheme itemTheme = new QItemTheme("itemTheme");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pleiades.entity.character.QTheItem item;

    public final QTheme theme;

    public QItemTheme(String variable) {
        this(ItemTheme.class, forVariable(variable), INITS);
    }

    public QItemTheme(Path<? extends ItemTheme> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemTheme(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemTheme(PathMetadata metadata, PathInits inits) {
        this(ItemTheme.class, metadata, inits);
    }

    public QItemTheme(Class<? extends ItemTheme> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.pleiades.entity.character.QTheItem(forProperty("item")) : null;
        this.theme = inits.isInitialized("theme") ? new QTheme(forProperty("theme")) : null;
    }

}

