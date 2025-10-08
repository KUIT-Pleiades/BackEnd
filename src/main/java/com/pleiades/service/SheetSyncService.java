package com.pleiades.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.pleiades.entity.character.TheItem;
import com.pleiades.entity.store.search.*;
import com.pleiades.repository.ColorRepository;
import com.pleiades.repository.KeywordRepository;
import com.pleiades.repository.ThemeRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.strings.ItemType;
import com.pleiades.util.GoogleSheetsUtil;
import com.pleiades.util.TextNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 색상 사전 시트 컬럼
 * 0: 색상(메인)
 * 1: 동의어CSV
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SheetSyncService {

    private final TheItemRepository theItemRepository;
    private final ColorRepository colorRepository;
    private final ThemeRepository themeRepository;
    private final KeywordRepository keywordRepository;

    private static final String SPREADSHEET_ID = "15n299QZR6vvQrca0bybf1ie5P-xbNkTa0DpmVHrGjko";
    private static final String ITEM_RANGE = "avatar_item_list!A2:J";
    private static final String COLOR_DICT_RANGE = "color_synonyms!A2:B";  // ← 실제 색상 사전 시트명으로 변경

    /** SyncItemSpreadsheet에서 주기적으로 호출 */
    @Transactional
    public void sync() {
        Sheets sheets = GoogleSheetsUtil.getSheetsService();

        try {
            // 1) 색상 먼저 동기화 (Color / ColorSynonyms)
            Map<String, Color> colorCache = syncColorDictionary(sheets);

            // 2) 아이템 시트 동기화 (컬러/테마/키워드 연결)
            syncItems(sheets, colorCache);

        } catch (Exception e) {
            log.error("[SheetSync] 전체 실패", e);
        }
    }

    /** 색상 사전(메인/동의어) 동기화: 캐시 반환 */
    private Map<String, Color> syncColorDictionary(Sheets sheets) throws Exception {
        ValueRange response = sheets.spreadsheets().values()
                .get(SPREADSHEET_ID, COLOR_DICT_RANGE)
                .execute();

        List<List<Object>> rows = response.getValues();
        if (rows == null || rows.isEmpty()) {
            log.info("[SheetSync] 색상 사전 시트가 비어 있음");
        }

        // 기존 DB 로드 후 캐시 준비
        Map<String, Color> colorCache = new HashMap<>();
        colorRepository.findAll().forEach(c ->
                colorCache.put(TextNormalizer.norm(c.getName()), c));

        int ok = 0, fail = 0;
        if (rows != null) {
            for (List<Object> row : rows) {
                try {
                    String mainColor = safe(row, 0);   // 메인 색상
                    String synCsv    = safe(row, 1);   // 동의어 CSV

                    if (mainColor.isBlank()) continue;

                    // 메인 색상 업서트
                    Color main = upsertColor(mainColor, colorCache);

                    // 동의어 업서트
                    if (!synCsv.isBlank()) {
                        for (String s : TextNormalizer.splitCsv(synCsv)) {
                            String norm = TextNormalizer.norm(s);
                            boolean exists = main.getSynonyms().stream()
                                    .anyMatch(cs -> TextNormalizer.eq(cs.getSynonyms(), norm));
                            if (!exists) {
                                main.getSynonyms().add(
                                        ColorSynonyms.builder()
                                                .synonyms(norm)
                                                .color(main)
                                                .build()
                                );
                            }
                        }
                        colorRepository.save(main);
                    }

                    ok++;
                } catch (Exception ex) {
                    fail++;
                    log.warn("[SheetSync] 색상 사전 행 처리 실패: {} / err={}", row, ex.toString());
                }
            }
        }

        log.info("[SheetSync] 색상 사전 동기화 완료 - 성공:{} 실패:{}", ok, fail);
        return colorCache;
    }

    /** 아이템 시트 동기화 (Color/Theme/Keyword 연결) */
    private void syncItems(Sheets sheets, Map<String, Color> colorCache) throws Exception {
        ValueRange response = sheets.spreadsheets().values()
                .get(SPREADSHEET_ID, ITEM_RANGE)
                .execute();

        List<List<Object>> rows = response.getValues();
        if (rows == null || rows.isEmpty()) {
            log.info("[SheetSync] 아이템 시트가 비어 있음");
            return;
        }

        // 테마 캐시 준비
        Map<String, Theme> themeCache = new HashMap<>();
        themeRepository.findAll().forEach(t ->
                themeCache.put(TextNormalizer.norm(t.getName()), t));

        int ok = 0, fail = 0;
        for (List<Object> row : rows) {
            try {
                upsertItemRow(row, colorCache, themeCache);
                ok++;
            } catch (Exception e) {
                fail++;
                log.warn("[SheetSync] 아이템 행 처리 실패: {} / err={}", row, e.toString());
            }
        }

        log.info("[SheetSync] 아이템 동기화 완료 - 성공:{} 실패:{}", ok, fail);
    }

    /** 한 줄(아이템) 업서트 */
    private void upsertItemRow(
            List<Object> row,
            Map<String, Color> colorCache,
            Map<String, Theme> themeCache
    ) {
        String index = safe(row, 0);
        String filename    = safe(row, 1);
        String createdAt   = safe(row, 2); // 미사용
        String toggle      = safe(row, 3);
        String type        = safe(row, 4);
        String priceStr    = safe(row, 5);
        String colorsCsv   = safe(row, 6); // CSV
        String themesCsv    = safe(row, 7); // CSV
        String keywordsCsv = safe(row, 8); // CSV
        String itemName    = safe(row, 9);

        log.info("\n======= item info =======\nindex: {}, filename: {}, createAt: {}, toggle: {}, type: {}, price: {}, itemName: {}", index, filename, createdAt, toggle, type, priceStr, itemName);

        if (itemName.isBlank()) {
            log.warn("[SheetSync] 아이템명 비어있어 스킵: {}", row);
            return;
        }

        if (priceStr.isBlank()) {
            log.warn("[SheetSync] 가격 비어있어 스킵: {}", row);
            return;
        }

        // 1) TheItem 업서트
        var found = theItemRepository.findByName(filename);

        List<String> required = List.of(
                ItemType.EYES.getType(), ItemType.NOSE.getType(), ItemType.MOUTH.getType(),
                ItemType.HAIR.getType(), ItemType.SKIN_COLOR.getType()
                );
        boolean isRequired = required.contains(type);

        boolean isBasic = priceStr.equals("0");

        TheItem item = found.orElseGet(() ->
                TheItem.builder()
                        .id(parseLong(index))
                        .name(filename)
                        .type(parseType(type))
                        .price(parseLong(priceStr))
                        .description(itemName)
                        .isRequired(isRequired)
                        .isBasic(isBasic)
                        .build()
        );
        if (found.isPresent()) {
            item.setName(filename);
            item.setType(parseType(type));
            item.setPrice(parseLong(priceStr));
            item.setDescription(itemName);
        }
        theItemRepository.save(item);

        // 2) 색상 연결 (별도 시트에서 미리 업서트된 colorCache 사용)
        if (!colorsCsv.isBlank()) {
            for (String raw : TextNormalizer.splitCsv(colorsCsv)) {
                Color c = upsertColor(raw, colorCache); // 캐시 → DB 재조회 최소화

                boolean hasColor = item.getItemColors().stream()
                        .anyMatch(ic -> Objects.equals(ic.getColor().getId(), c.getId()));
                if (!hasColor) {
                    item.getItemColors().add(
                            ItemColor.builder().item(item).color(c).build()
                    );
                }
            }
        }

        // 3) 테마 연결 (CSV)
        if (!themesCsv.isBlank()) {
            for (String raw : TextNormalizer.splitCsv(themesCsv)) {
                Theme theme = upsertTheme(raw, themeCache);

                boolean hasTheme = item.getItemThemes().stream()
                        .anyMatch(it -> Objects.equals(it.getTheme().getId(), theme.getId()));

                if (!hasTheme) {
                    item.getItemThemes().add(
                            ItemTheme.builder()
                                    .item(item)
                                    .theme(theme)
                                    .build()
                    );
                }
            }
        }

        // 4) 키워드 연결 (CSV -> Keyword 마스터 업서트 -> ItemKeyword 연결)
        if (!keywordsCsv.isBlank()) {
            for (String raw : TextNormalizer.splitCsv(keywordsCsv)) {
                String kwName = TextNormalizer.norm(raw);

                Keyword kw = keywordRepository.findByName(kwName)
                        .orElseGet(() -> keywordRepository.save(
                                Keyword.builder().name(kwName).build()
                        ));

                // 조인 엔티티로 중복 연결 방지
                boolean hasKw = item.getItemKeywords().stream()
                        .anyMatch(ik -> Objects.equals(ik.getKeyword().getId(), kw.getId()));
                if (!hasKw) {
                    item.getItemKeywords().add(
                            ItemKeyword.builder().item(item).keyword(kw).build()
                    );
                }
            }
        }

        theItemRepository.save(item);
    }

    private Color upsertColor(String raw, Map<String, Color> cache) {
        String key = TextNormalizer.norm(raw);
        Color cached = cache.get(key);
        if (cached != null) return cached;

        Color c = colorRepository.findByName(key)
                .orElseGet(() -> Color.builder().name(key).build());
        if (c.getId() == null) c = colorRepository.save(c);

        cache.put(key, c);
        return c;
    }

    private Theme upsertTheme(String raw, Map<String, Theme> cache) {
        String key = TextNormalizer.norm(raw);

        Theme cached = cache.get(key);
        if (cached != null) return cached;

        Theme t = themeRepository.findByName(key)
                .orElseGet(() -> themeRepository.save(
                        Theme.builder().name(key).build()
                ));

        cache.put(key, t);
        return t;
    }


    private String safe(List<Object> row, int i) {
        return (i < row.size() && row.get(i) != null) ? String.valueOf(row.get(i)).trim() : "";
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s.replaceAll("[^0-9-]", "")); }
        catch (Exception e) { return 0L; }
    }

    private ItemType parseType(String s) {
        try {
            return ItemType.fromString(s.trim());
        } catch (IllegalArgumentException e) {
            log.warn("[SheetSync] 알 수 없는 ItemType: '{}', 기본값으로 대체", s);
            return ItemType.DEFAULT;
        }
    }
}