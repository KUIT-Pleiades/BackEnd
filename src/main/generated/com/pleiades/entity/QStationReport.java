package com.pleiades.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStationReport is a Querydsl query type for StationReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStationReport extends EntityPathBase<StationReport> {

    private static final long serialVersionUID = -2004713715L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStationReport stationReport = new QStationReport("stationReport");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReport report;

    public final QStation station;

    public QStationReport(String variable) {
        this(StationReport.class, forVariable(variable), INITS);
    }

    public QStationReport(Path<? extends StationReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStationReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStationReport(PathMetadata metadata, PathInits inits) {
        this(StationReport.class, metadata, inits);
    }

    public QStationReport(Class<? extends StationReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.report = inits.isInitialized("report") ? new QReport(forProperty("report"), inits.get("report")) : null;
        this.station = inits.isInitialized("station") ? new QStation(forProperty("station"), inits.get("station")) : null;
    }

}

