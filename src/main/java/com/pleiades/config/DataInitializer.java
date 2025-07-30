//package com.pleiades.config;
//
//import com.pleiades.entity.*;
//import com.pleiades.repository.*;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//import java.util.Objects;
//
//@RequiredArgsConstructor
//@Configuration
//public class DataInitializer {
//
//    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
//
//    private final StarBackgroundRepository starBackgroundRepository;
//    private final QuestionRepository questionRepository;
//    private final StationBackgroundRepository stationBackgroundRepository;
//
//    @Transactional
//    @PostConstruct
//    public void initData() {
//        saveStarBackground();
//        saveQuestion();
//        saveStationBackground();
//    }
//
//    @Transactional
//    protected void saveQuestion() {
//        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("questions.txt");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
//
//            reader.lines()
//                    .map(Question::new)
//                    .forEach(questionRepository::save);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }
//
//    @Transactional
//    protected void saveStarBackground() {
//        String[] bgimgs = {"bg_star_1.png", "bg_star_2.png", "bg_star_3.png", "bg_star_4.png", "bg_star_5.png"};
//        for (String name : bgimgs) {
//            StarBackground bgimg = new StarBackground();
//            bgimg.setName(name);
//            starBackgroundRepository.save(bgimg);
//        }
//    }
//
//    @Transactional
//    protected void saveStationBackground() {
//        String[] bgimgs = {"bg_station_1.png", "bg_station_2.png", "bg_station_3.png", "bg_station_4.png", "bg_station_5.png", "bg_station_6.png"};
//        for (String name : bgimgs) {
//            StationBackground bgimg = new StationBackground();
//            bgimg.setName(name);
//            stationBackgroundRepository.save(bgimg);
//        }
//        stationBackgroundRepository.flush();
//    }
//}
