package com.pleiades.entity.store.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTheme is a Querydsl query type for Theme
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTheme extends EntityPathBase<Theme> {

    private static final long serialVersionUID = -1932714677L;

    public static final QTheme theme = new QTheme("theme");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ItemTheme, QItemTheme> itemThemes = this.<ItemTheme, QItemTheme>createList("itemThemes", ItemTheme.class, QItemTheme.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public QTheme(String variable) {
        super(Theme.class, forVariable(variable));
    }

    public QTheme(Path<? extends Theme> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTheme(PathMetadata metadata) {
        super(Theme.class, metadata);
    }

}

