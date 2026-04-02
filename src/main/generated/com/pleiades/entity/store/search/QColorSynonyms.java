package com.pleiades.entity.store.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QColorSynonyms is a Querydsl query type for ColorSynonyms
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QColorSynonyms extends EntityPathBase<ColorSynonyms> {

    private static final long serialVersionUID = -222639651L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QColorSynonyms colorSynonyms = new QColorSynonyms("colorSynonyms");

    public final QColor color;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath synonyms = createString("synonyms");

    public QColorSynonyms(String variable) {
        this(ColorSynonyms.class, forVariable(variable), INITS);
    }

    public QColorSynonyms(Path<? extends ColorSynonyms> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QColorSynonyms(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QColorSynonyms(PathMetadata metadata, PathInits inits) {
        this(ColorSynonyms.class, metadata, inits);
    }

    public QColorSynonyms(Class<? extends ColorSynonyms> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.color = inits.isInitialized("color") ? new QColor(forProperty("color")) : null;
    }

}

