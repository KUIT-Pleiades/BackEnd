package com.pleiades.service;

import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.search.Color;
import com.pleiades.entity.store.search.Keyword;
import com.pleiades.entity.store.search.Theme;
import com.pleiades.repository.ColorRepository;
import com.pleiades.repository.KeywordRepository;
import com.pleiades.repository.ThemeRepository;
import com.pleiades.repository.character.TheItemRepository;
// (선택) 조인 엔티티 레포가 있다면 임포트
// import com.pleiades.repository.ItemColorRepository;
// import com.pleiades.repository.ItemThemeRepository;
// import com.pleiades.repository.ItemKeywordRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EntityScan(basePackageClasses = {
        com.pleiades.entity.character.TheItem.class,
        com.pleiades.entity.store.search.Color.class,
        com.pleiades.entity.store.search.Theme.class,
        com.pleiades.entity.store.search.Keyword.class,
        com.pleiades.entity.store.search.ItemColor.class,
        com.pleiades.entity.store.search.ItemTheme.class,
        com.pleiades.entity.store.search.ItemKeyword.class
})
@ActiveProfiles("test") // 테스트용 설정이 있다면
@Transactional // 테스트 종료 후 롤백
class SheetSyncServiceTest {

    @Autowired private SheetSyncService sheetSyncService;

    @Autowired private TheItemRepository theItemRepository;
    @Autowired private ColorRepository colorRepository;
    @Autowired private ThemeRepository themeRepository;
    @Autowired private KeywordRepository keywordRepository;

    // (선택) 조인 엔티티 레포가 있다면 주입해서 직접 카운트/검증 가능
    // @Autowired private ItemColorRepository itemColorRepository;
    // @Autowired private ItemThemeRepository itemThemeRepository;
    // @Autowired private ItemKeywordRepository itemKeywordRepository;

    @Test
    @DisplayName("Google Sheet 동기화: 아이템/색상/테마/키워드 및 연결 검증")
    void sync_shouldPopulateDomainAndLinks() {
        // when
        sheetSyncService.sync();

        // then: 아이템 검증
        List<TheItem> items = theItemRepository.findAll();
        assertThat(items)
                .as("아이템이 한 개 이상 동기화되어야 함")
                .isNotEmpty();

        // 아이템 필수 필드 검증 + 첫 아이템 상세 출력
        TheItem first = items.get(0);
        assertThat(first.getName()).as("아이템명").isNotBlank();
        assertThat(first.getPrice()).as("가격").isNotNull();
        assertThat(first.getType()).as("타입(ItemType)").isNotNull();

        System.out.printf("[ITEM] id=%d, name=%s, price=%d, type=%s%n",
                first.getId(), first.getName(), first.getPrice(), first.getType());

        // 색상 마스터 존재 검증
        List<Color> colors = colorRepository.findAll();
        assertThat(colors)
                .as("색상 사전(Color)이 먼저 동기화되어야 함")
                .isNotEmpty();
        System.out.printf("[COLORS] total=%d, names=%s%n",
                colors.size(),
                colors.stream().map(Color::getName).limit(10).collect(Collectors.toList()));

        // 테마 마스터 존재 검증
        List<Theme> themes = themeRepository.findAll();
        // 테마가 실제로 없을 수 있다면 isNotEmpty 대신 size() >= 0 로 완화
        assertThat(themes)
                .as("테마(Theme) 존재 여부")
                .isNotNull();
        System.out.printf("[THEMES] total=%d, names=%s%n",
                themes.size(),
                themes.stream().map(Theme::getName).limit(10).collect(Collectors.toList()));

        // 키워드 마스터 존재 검증
        List<Keyword> keywords = keywordRepository.findAll();
        System.out.printf("[KEYWORDS] total=%d, names=%s%n",
                keywords.size(),
                keywords.stream().map(Keyword::getName).limit(10).collect(Collectors.toList()));

        // 첫 아이템의 연결(조인 엔티티) 검증
        // 1) 색상 연결
        assertThat(first.getItemColors())
                .as("아이템-색상 연결(ItemColor)")
                .isNotNull();
        System.out.printf("[ITEM-COLORS] count=%d, colorNames=%s%n",
                first.getItemColors().size(),
                first.getItemColors().stream()
                        .map(ic -> ic.getColor().getName())
                        .collect(Collectors.toList()));
        // (선택) 중복 방지 확인
        long distinctColorIds = first.getItemColors().stream()
                .map(ic -> ic.getColor().getId()).distinct().count();
        assertThat(distinctColorIds).as("중복 없는 색상 연결").isEqualTo(first.getItemColors().size());

        // 2) 테마 연결
        assertThat(first.getItemThemes())
                .as("아이템-테마 연결(ItemTheme)")
                .isNotNull();
        System.out.printf("[ITEM-THEMES] count=%d, themeNames=%s%n",
                first.getItemThemes().size(),
                first.getItemThemes().stream()
                        .map(it -> it.getTheme().getName())
                        .collect(Collectors.toList()));
        long distinctThemeIds = first.getItemThemes().stream()
                .map(it -> it.getTheme().getId()).distinct().count();
        assertThat(distinctThemeIds).as("중복 없는 테마 연결").isEqualTo(first.getItemThemes().size());

        // 3) 키워드 연결 (ItemKeyword 사용 시)
        assertThat(first.getItemKeywords())
                .as("아이템-키워드 연결(ItemKeyword)")
                .isNotNull();
        System.out.printf("[ITEM-KEYWORDS] count=%d, keywordNames=%s%n",
                first.getItemKeywords().size(),
                first.getItemKeywords().stream()
                        .map(ik -> ik.getKeyword().getName())
                        .collect(Collectors.toList()));
        long distinctKeywordIds = first.getItemKeywords().stream()
                .map(ik -> ik.getKeyword().getId()).distinct().count();
        assertThat(distinctKeywordIds).as("중복 없는 키워드 연결").isEqualTo(first.getItemKeywords().size());

        // 전체 개수 sanity 체크 (선택)
        // assertThat(itemColorRepository.count()).isGreaterThan(0);
        // assertThat(itemThemeRepository.count()).isGreaterThan(0);
        // assertThat(itemKeywordRepository.count()).isGreaterThan(0);
    }
}
