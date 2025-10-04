package com.pleiades.entity.store.search;

import com.pleiades.entity.character.TheItem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_keywords", uniqueConstraints = @UniqueConstraint(name = "uk_item_keyword", columnNames = {"item_id", "keyword_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private TheItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;
}
