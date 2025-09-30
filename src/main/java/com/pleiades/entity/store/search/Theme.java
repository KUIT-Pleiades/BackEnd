package com.pleiades.entity.store.search;

import com.pleiades.strings.ItemType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemTheme> itemThemes = new ArrayList<>();
}
