package com.pleiades.repository.character;

import com.pleiades.entity.character.QTheItem;
import com.pleiades.entity.character.TheItem;
import com.pleiades.strings.ItemType;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TheItemRepositoryImpl implements TheItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TheItem> findNotBasicItemsByTypes(List<ItemType> types) {
        QTheItem item = QTheItem.theItem;

        ItemType[] allTypes = ItemType.values();
        CaseBuilder.Cases<Integer, NumberExpression<Integer>> caseExpr =
                new CaseBuilder().when(item.type.eq(allTypes[0])).then(0);
        for (int i = 1; i < allTypes.length; i++) {
            caseExpr = caseExpr.when(item.type.eq(allTypes[i])).then(i);
        }
        NumberExpression<Integer> typeOrder = caseExpr.otherwise(allTypes.length);

        return queryFactory
                .selectFrom(item)
                .where(
                        item.isBasic.isFalse(),
                        item.type.in(types)
                )
                .orderBy(typeOrder.asc())
                .fetch();
    }
}
