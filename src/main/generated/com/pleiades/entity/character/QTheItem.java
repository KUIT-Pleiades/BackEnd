package com.pleiades.entity.character;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTheItem is a Querydsl query type for TheItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTheItem extends EntityPathBase<TheItem> {

    private static final long serialVersionUID = 297050724L;

    public static final QTheItem theItem = new QTheItem("theItem");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isBasic = createBoolean("isBasic");

    public final BooleanPath isRequired = createBoolean("isRequired");

    public final ListPath<com.pleiades.entity.store.search.ItemColor, com.pleiades.entity.store.search.QItemColor> itemColors = this.<com.pleiades.entity.store.search.ItemColor, com.pleiades.entity.store.search.QItemColor>createList("itemColors", com.pleiades.entity.store.search.ItemColor.class, com.pleiades.entity.store.search.QItemColor.class, PathInits.DIRECT2);

    public final ListPath<com.pleiades.entity.store.search.ItemKeyword, com.pleiades.entity.store.search.QItemKeyword> itemKeywords = this.<com.pleiades.entity.store.search.ItemKeyword, com.pleiades.entity.store.search.QItemKeyword>createList("itemKeywords", com.pleiades.entity.store.search.ItemKeyword.class, com.pleiades.entity.store.search.QItemKeyword.class, PathInits.DIRECT2);

    public final ListPath<com.pleiades.entity.store.search.ItemTheme, com.pleiades.entity.store.search.QItemTheme> itemThemes = this.<com.pleiades.entity.store.search.ItemTheme, com.pleiades.entity.store.search.QItemTheme>createList("itemThemes", com.pleiades.entity.store.search.ItemTheme.class, com.pleiades.entity.store.search.QItemTheme.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final EnumPath<com.pleiades.strings.ItemType> type = createEnum("type", com.pleiades.strings.ItemType.class);

    public final ListPath<CharacterItem, QCharacterItem> usedByCharacters = this.<CharacterItem, QCharacterItem>createList("usedByCharacters", CharacterItem.class, QCharacterItem.class, PathInits.DIRECT2);

    public QTheItem(String variable) {
        super(TheItem.class, forVariable(variable));
    }

    public QTheItem(Path<? extends TheItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTheItem(PathMetadata metadata) {
        super(TheItem.class, metadata);
    }

}

