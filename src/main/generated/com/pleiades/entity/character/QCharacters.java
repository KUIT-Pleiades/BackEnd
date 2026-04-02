package com.pleiades.entity.character;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCharacters is a Querydsl query type for Characters
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCharacters extends EntityPathBase<Characters> {

    private static final long serialVersionUID = -342601942L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCharacters characters = new QCharacters("characters");

    public final ListPath<CharacterItem, QCharacterItem> characterItems = this.<CharacterItem, QCharacterItem>createList("characterItems", CharacterItem.class, QCharacterItem.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.pleiades.entity.QUser user;

    public QCharacters(String variable) {
        this(Characters.class, forVariable(variable), INITS);
    }

    public QCharacters(Path<? extends Characters> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCharacters(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCharacters(PathMetadata metadata, PathInits inits) {
        this(Characters.class, metadata, inits);
    }

    public QCharacters(Class<? extends Characters> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.pleiades.entity.QUser(forProperty("user")) : null;
    }

}

