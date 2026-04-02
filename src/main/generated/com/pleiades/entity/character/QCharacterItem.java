package com.pleiades.entity.character;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCharacterItem is a Querydsl query type for CharacterItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCharacterItem extends EntityPathBase<CharacterItem> {

    private static final long serialVersionUID = -1613295332L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCharacterItem characterItem = new QCharacterItem("characterItem");

    public final QCharacters character;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTheItem item;

    public QCharacterItem(String variable) {
        this(CharacterItem.class, forVariable(variable), INITS);
    }

    public QCharacterItem(Path<? extends CharacterItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCharacterItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCharacterItem(PathMetadata metadata, PathInits inits) {
        this(CharacterItem.class, metadata, inits);
    }

    public QCharacterItem(Class<? extends CharacterItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.character = inits.isInitialized("character") ? new QCharacters(forProperty("character"), inits.get("character")) : null;
        this.item = inits.isInitialized("item") ? new QTheItem(forProperty("item")) : null;
    }

}

