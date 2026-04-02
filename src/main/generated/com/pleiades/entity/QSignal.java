package com.pleiades.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSignal is a Querydsl query type for Signal
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSignal extends EntityPathBase<Signal> {

    private static final long serialVersionUID = -249678141L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSignal signal = new QSignal("signal");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> imageIndex = createNumber("imageIndex", Integer.class);

    public final QUser receiver;

    public final QUser sender;

    public QSignal(String variable) {
        this(Signal.class, forVariable(variable), INITS);
    }

    public QSignal(Path<? extends Signal> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSignal(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSignal(PathMetadata metadata, PathInits inits) {
        this(Signal.class, metadata, inits);
    }

    public QSignal(Class<? extends Signal> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new QUser(forProperty("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new QUser(forProperty("sender")) : null;
    }

}

