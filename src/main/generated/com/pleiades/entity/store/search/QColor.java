package com.pleiades.entity.store.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QColor is a Querydsl query type for Color
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QColor extends EntityPathBase<Color> {

    private static final long serialVersionUID = -1948199195L;

    public static final QColor color = new QColor("color");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ItemColor, QItemColor> itemColors = this.<ItemColor, QItemColor>createList("itemColors", ItemColor.class, QItemColor.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final ListPath<ColorSynonyms, QColorSynonyms> synonyms = this.<ColorSynonyms, QColorSynonyms>createList("synonyms", ColorSynonyms.class, QColorSynonyms.class, PathInits.DIRECT2);

    public QColor(String variable) {
        super(Color.class, forVariable(variable));
    }

    public QColor(Path<? extends Color> path) {
        super(path.getType(), path.getMetadata());
    }

    public QColor(PathMetadata metadata) {
        super(Color.class, metadata);
    }

}

