package com.pleiades.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QKakaoToken is a Querydsl query type for KakaoToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QKakaoToken extends EntityPathBase<KakaoToken> {

    private static final long serialVersionUID = -134642287L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QKakaoToken kakaoToken = new QKakaoToken("kakaoToken");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.sql.Timestamp> lastUpdated = createDateTime("lastUpdated", java.sql.Timestamp.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final QUser user;

    public QKakaoToken(String variable) {
        this(KakaoToken.class, forVariable(variable), INITS);
    }

    public QKakaoToken(Path<? extends KakaoToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QKakaoToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QKakaoToken(PathMetadata metadata, PathInits inits) {
        this(KakaoToken.class, metadata, inits);
    }

    public QKakaoToken(Class<? extends KakaoToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

