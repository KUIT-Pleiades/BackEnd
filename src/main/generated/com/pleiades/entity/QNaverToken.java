package com.pleiades.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNaverToken is a Querydsl query type for NaverToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNaverToken extends EntityPathBase<NaverToken> {

    private static final long serialVersionUID = 607796580L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNaverToken naverToken = new QNaverToken("naverToken");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> lastUpdated = createNumber("lastUpdated", Long.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final QUser user;

    public QNaverToken(String variable) {
        this(NaverToken.class, forVariable(variable), INITS);
    }

    public QNaverToken(Path<? extends NaverToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNaverToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNaverToken(PathMetadata metadata, PathInits inits) {
        this(NaverToken.class, metadata, inits);
    }

    public QNaverToken(Class<? extends NaverToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

