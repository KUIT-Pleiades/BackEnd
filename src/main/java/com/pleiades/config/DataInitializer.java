package com.pleiades.config;

import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.entity.character.Item.*;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.outfit.Bottom;
import com.pleiades.entity.character.outfit.Shoes;
import com.pleiades.entity.character.outfit.Top;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.repository.character.face.ExpressionRepository;
import com.pleiades.repository.character.face.HairRepository;
import com.pleiades.repository.character.face.SkinRepository;
import com.pleiades.repository.character.item.*;
import com.pleiades.repository.character.outfit.BottomRepository;
import com.pleiades.repository.character.outfit.ShoesRepository;
import com.pleiades.repository.character.outfit.TopRepository;
import com.pleiades.strings.FriendStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final UserStationRepository userStationRepository;
    private final FriendRepository friendRepository;
    private final NaverTokenRepository naverTokenRepository;
    private final UserHistoryRepository userHistoryRepository;

    private final SkinRepository skinRepository;
    private final ExpressionRepository expressionRepository;
    private final HairRepository hairRepository;

    private final HeadRepository headRepository;
    private final NeckRepository neckRepository;
    private final EyesRepository eyesRepository;
    private final EarsRepository earsRepository;
    private final LeftHandRepository leftHandRepository;
    private final RightHandRepository rightHandRepository;
    private final LeftWristRepository leftWristRepository;
    private final RightWristRepository rightWristRepository;

    private final TopRepository topRepository;
    private final BottomRepository bottomRepository;
    private final ShoesRepository shoesRepository;

    private final StarBackgroundRepository starBackgroundRepository;
    private final QuestionRepository questionRepository;

    private final String IPFS_URL = System.getenv("IPFS_URL");
    private final StationRepository stationRepository;
    private final ReportRepository reportRepository;
    private final StationBackgroundRepository stationBackgroundRepository;
    private final StationQuestionRepository stationQuestionRepository;
    private final StationReportRepository stationReportRepository;


    @PostConstruct
    public void initData() throws IOException {
        saveNaverToken();
        saveUser(); saveFriend();
        saveSkin(); saveExpression(); saveHair();
        saveItem();
        saveTop(); saveBottom(); saveShoes();
        saveStarBackground();
        saveStar();

        saveQuestion();
        saveReport();
        saveStationBackground();
        saveStation();
        saveUserStation();
        saveStationQuestion();
        saveUserHistory();
        saveStationReport();
    }

    private void saveNaverToken(){
        List<NaverToken> naverTokens = List.of(

                //new NaverToken(null,null, "wook2442@naver.com", null, "refresh1", 1L),
//                new NaverToken(null, null, "yuna569@naver.com", null, "refresh2", 1L),
                new NaverToken(null, null, "danpung628@gmail.com", null, "refresh3", 1L),
                // new NaverToken(null, null, "yh81260@naver.com", null, "refresh4", 1L),
                new NaverToken(null, null, "yona0209@naver.com", null, "refresh5", 1L),
                new NaverToken(null, null, "user1@naver.com", null, "refresh6", 1L),
                new NaverToken(null, null, "user2@naver.com", null, "refresh7", 1L),
                new NaverToken(null, null, "user3@naver.com", null, "refresh8", 1L),
                new NaverToken(null, null, "user4@naver.com", null, "refresh8", 1L),
                new NaverToken(null, null, "user5@naver.com", null, "refresh8", 1L),
                new NaverToken(null, null, "user6@naver.com", null, "refresh8", 1L),
                new NaverToken(null, null, "user7@naver.com", null, "refresh8", 1L)
        );
        naverTokenRepository.saveAll(naverTokens);
        naverTokenRepository.flush();
    }
    private void saveUserStation() {
        List<UserStation> userStations = List.of(
                // 첫 번째 정거장 (ABCDEF)
                new UserStation(new UserStationId("user1", "ABCDEF"), userRepository.findById("user1").orElseThrow(),
                        stationRepository.findById("ABCDEF").orElseThrow(), true, LocalDateTime.now(), false, 25f, 50f),

                new UserStation(new UserStationId("user2", "ABCDEF"), userRepository.findById("user2").orElseThrow(),
                        stationRepository.findById("ABCDEF").orElseThrow(), false, LocalDateTime.now(), false, 50f, 50f),

                new UserStation(new UserStationId("danpung628", "ABCDEF"), userRepository.findById("danpung628").orElseThrow(),
                        stationRepository.findById("ABCDEF").orElseThrow(), false, LocalDateTime.now(), false, 75f, 50f),

                new UserStation(new UserStationId("hyungyu", "ABCDEF"), userRepository.findById("hyungyu").orElseThrow(),
                        stationRepository.findById("ABCDEF").orElseThrow(), false, LocalDateTime.now(), false, 25f, 70f),

                // 두 번째 정거장 (BC123D)
                new UserStation(new UserStationId("user1", "BC123D"), userRepository.findById("user1").orElseThrow(),
                        stationRepository.findById("BC123D").orElseThrow(), true, LocalDateTime.now(), false, 25f, 50f),

                new UserStation(new UserStationId("user2", "BC123D"), userRepository.findById("user2").orElseThrow(),
                        stationRepository.findById("BC123D").orElseThrow(), false, LocalDateTime.now(), false, 50f, 50f),

                new UserStation(new UserStationId("user3", "BC123D"), userRepository.findById("user3").orElseThrow(),
                        stationRepository.findById("BC123D").orElseThrow(), false, LocalDateTime.now(), false, 75f, 50f),

                new UserStation(new UserStationId("hyungyu", "BC123D"), userRepository.findById("hyungyu").orElseThrow(),
                        stationRepository.findById("BC123D").orElseThrow(), false, LocalDateTime.now(), false, 25f, 70f),

                // 세 번째 정거장 (OPQ4R5)
                new UserStation(new UserStationId("user3", "OPQ4R5"), userRepository.findById("user3").orElseThrow(),
                        stationRepository.findById("OPQ4R5").orElseThrow(), true, LocalDateTime.now(), false, 25f, 50f),

                new UserStation(new UserStationId("hyungyu", "OPQ4R5"), userRepository.findById("hyungyu").orElseThrow(),
                        stationRepository.findById("OPQ4R5").orElseThrow(), false, LocalDateTime.now(), false, 50f, 50f),

                new UserStation(new UserStationId("user1", "OPQ4R5"), userRepository.findById("user1").orElseThrow(),
                        stationRepository.findById("OPQ4R5").orElseThrow(), false, LocalDateTime.now(), false, 75f, 50f),

                // 네 번째 정거장 (VW0XYZ)
                new UserStation(new UserStationId("hyungyu", "VW0XYZ"), userRepository.findById("hyungyu").orElseThrow(),
                        stationRepository.findById("VW0XYZ").orElseThrow(), true, LocalDateTime.now(), false, 25f, 50f),

                new UserStation(new UserStationId("user2", "VW0XYZ"), userRepository.findById("user2").orElseThrow(),
                        stationRepository.findById("VW0XYZ").orElseThrow(), false, LocalDateTime.now(), false, 50f, 50f),

                new UserStation(new UserStationId("user1", "VW0XYZ"), userRepository.findById("user1").orElseThrow(),
                        stationRepository.findById("VW0XYZ").orElseThrow(), false, LocalDateTime.now(), false, 75f, 50f),

                // 다섯 번째 정거장 (LYHENO)
                new UserStation(new UserStationId("user7", "LYHENO"), userRepository.findById("user7").orElseThrow(),
                        stationRepository.findById("LYHENO").orElseThrow(), true, LocalDateTime.now(), false, 25f, 50f),

                new UserStation(new UserStationId("user4", "LYHENO"), userRepository.findById("user4").orElseThrow(),
                        stationRepository.findById("LYHENO").orElseThrow(), false, LocalDateTime.now(), false, 50f, 50f),

                new UserStation(new UserStationId("user5", "LYHENO"), userRepository.findById("user5").orElseThrow(),
                        stationRepository.findById("LYHENO").orElseThrow(), false, LocalDateTime.now(), false, 75f, 50f),

                new UserStation(new UserStationId("user1", "LYHENO"), userRepository.findById("user1").orElseThrow(),
                        stationRepository.findById("LYHENO").orElseThrow(), false, LocalDateTime.now(), false, 25f, 70f),

                new UserStation(new UserStationId("hyungyu", "LYHENO"), userRepository.findById("hyungyu").orElseThrow(),
                        stationRepository.findById("LYHENO").orElseThrow(), false, LocalDateTime.now(), false, 50f, 70f),

                // 현규 정거장
                new UserStation(new UserStationId("hyungyu", "QWERTY"), userRepository.findById("hyungyu").orElseThrow(),
                        stationRepository.findById("QWERTY").orElseThrow(), true, LocalDateTime.now(), false, 25f, 50f),

                new UserStation(new UserStationId("hyungyu", "QWERYT"), userRepository.findById("hyungyu").orElseThrow(),
                        stationRepository.findById("QWERYT").orElseThrow(), true, LocalDateTime.now(), false, 25f, 50f)
                );
        userStationRepository.saveAll(userStations);
    }

    private void saveStar() {
        List<User> users = userRepository.findAll();
        List<StarBackground> starBackgrounds = starBackgroundRepository.findAll();
        List<Star> stars = List.of(
                Star.builder().user(users.get(0)).background(starBackgrounds.get(0)).build(),
                Star.builder().user(users.get(1)).background(starBackgrounds.get(1)).build(),
                Star.builder().user(users.get(2)).background(starBackgrounds.get(2)).build(),
                Star.builder().user(users.get(3)).background(starBackgrounds.get(3)).build(),
                Star.builder().user(users.get(4)).background(starBackgrounds.get(4)).build(),
                Star.builder().user(users.get(5)).background(starBackgrounds.get(0)).build(),
                Star.builder().user(users.get(6)).background(starBackgrounds.get(1)).build(),
                Star.builder().user(users.get(7)).background(starBackgrounds.get(2)).build(),
                Star.builder().user(users.get(8)).background(starBackgrounds.get(3)).build()
//                Star.builder().user(users.get(9)).background(starBackgrounds.get(4)).build()
                // Star.builder().user(users.get(10)).background(starBackgrounds.get(0)).build(),
                // Star.builder().user(users.get(11)).background(starBackgrounds.get(1)).build()
        );
    }

    private void saveStation() {
        List<StationBackground> bgs = stationBackgroundRepository.findAll();
        List<Station> stations = List.of(
                new Station("ABCDEF", "플아데", "hi", 4, LocalDateTime.now(), "user1", LocalTime.of(9,0,0), bgs.get(0)),
                new Station("BC123D", "컴공22", "hi", 4, LocalDateTime.now(), "user1", LocalTime.of(9,0,0),bgs.get(1)),
                new Station("OPQ4R5", "귀요미들", "hi", 3, LocalDateTime.now(), "user3", LocalTime.of(9,0,0),bgs.get(2)),
                new Station("VW0XYZ", "넵", "hi", 3, LocalDateTime.now(), "hyungyu", LocalTime.of(9,0,0),bgs.get(0)),
                new Station("LYHENO", "꺅", "hi", 5, LocalDateTime.now(), "user7", LocalTime.of(9,0,0),bgs.get(3)),
                new Station("QWERTY", "정거장이다", "hi", 1, LocalDateTime.now(), "hyungyu", LocalTime.of(9,0,0),bgs.get(1)),
                new Station("QWERYT", "오", "hi", 1, LocalDateTime.now(), "hyungyu", LocalTime.of(9,0,0),bgs.get(2))
        );
        stationRepository.saveAll(stations);
        stationRepository.flush();
    }

    private void saveUserHistory() {
        User hyungyu = userRepository.findById("hyungyu")
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 최근 검색 기록에 추가할 사용자 목록 (자기 자신 제외)
        List<User> searchedUsers = userRepository.findAll().stream()
                .filter(user -> !user.getId().equals("hyungyu"))
                .toList();

        // UserHistory 엔티티 생성
        List<UserHistory> userHistories = searchedUsers.stream()
                .map(user -> UserHistory.builder()
                        .current(hyungyu)
                        .searched(user)
                        .searchCount(1) // 검색 횟수 초기값
                        .isFriend(friendRepository.isFriend(hyungyu, user, FriendStatus.ACCEPTED))
                        .updatedAt(LocalDateTime.now())
                        .build())
                .toList();

        userHistoryRepository.saveAll(userHistories);
    }

    private void saveUser() {
        List<User> users = List.of(
                //new User("woogie", "wook2442@naver.com", "강연욱이", LocalDate.of(2000, 2, 4), LocalDate.of(2025, 2, 14), "profile_01", "character_01", "refresh"),
//                new User("yuna1217", "yuna569@naver.com", "윤아", LocalDate.of(2003, 12, 17), LocalDate.of(2025, 2, 14), "profile_01", "character_01", "refresh"),
                new User("danpung628", "danpung628@gmail.com", "원우", LocalDate.of(2000, 6, 28), LocalDate.of(2025, 2, 14), "profile_01", "character_01", "refresh"),
                //new User("lylylylh", "yh81260@naver.com", "윤희", LocalDate.of(2002, 10, 4), LocalDate.of(2025, 2, 3), "profile_01", "character_01", "refresh1"),
                new User("hyungyu", "yona0209@naver.com", "현규", LocalDate.of(2002, 2, 9), LocalDate.of(2025, 2, 16), "profile_01", "character_01", "refresh"),
                new User("user1", "user1@naver.com", "나연", LocalDate.of(2003, 9, 1), LocalDate.of(2025, 2, 1), "profile_02", "character_02", "refresh2"),
                new User("user2", "user2@naver.com", "정윤", LocalDate.of(2002, 1, 29), LocalDate.of(2025, 2, 4), "profile_02", "character_02", "refresh2"),
                new User("user3", "user3@naver.com", "세진", LocalDate.of(2002, 4, 17), LocalDate.of(2025, 2, 5), "profile_03", "character_03", "refresh3"),
                new User("user4", "user4@naver.com", "영은", LocalDate.of(2002, 12, 2), LocalDate.of(2025, 2, 3), "profile_04", "character_04", "refresh4"),
                new User("user5", "user5@naver.com", "상은", LocalDate.of(2002, 9, 27), LocalDate.of(2025, 2, 6), "profile_05", "character_05", "refresh5"),
                new User("user6", "user6@naver.com", "태은", LocalDate.of(2002, 1, 3), LocalDate.of(2025, 2, 7), "profile_06", "character_06", "refresh6"),
                new User("user7", "user7@naver.com", "하연", LocalDate.of(2002, 11, 14), LocalDate.of(2025, 2, 4), "profile_07", "character_07", "refresh7")
        );

        userRepository.saveAll(users);
        userRepository.flush();
    }

    private void saveFriend() {
        List<Friend> friends = List.of(
                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("user2").orElseThrow())
                        .receiver(userRepository.findById("hyungyu").orElseThrow()).build(),
                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("user1").orElseThrow())
                        .receiver(userRepository.findById("hyungyu").orElseThrow()).build(),
                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("hyungyu").orElseThrow())
                        .receiver(userRepository.findById("user3").orElseThrow()).build(),
                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("hyungyu").orElseThrow())
                        .receiver(userRepository.findById("user4").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("hyungyu").orElseThrow())
                        .receiver(userRepository.findById("user5").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("user6").orElseThrow())
                        .receiver(userRepository.findById("hyungyu").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("hyungyu").orElseThrow())
                        .receiver(userRepository.findById("user7").orElseThrow()).build(),

//                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
//                        .sender(userRepository.findById("hyungyu").orElseThrow())
//                        .receiver(userRepository.findById("woogie").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("hyungyu").orElseThrow())
                        .receiver(userRepository.findById("danpung628").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 11, 19, 46, 5))
                        .sender(userRepository.findById("user3").orElseThrow())
                        .receiver(userRepository.findById("user1").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 12, 9, 46, 5))
                        .sender(userRepository.findById("user1").orElseThrow())
                        .receiver(userRepository.findById("user4").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 12, 11, 46, 5))
                        .sender(userRepository.findById("user1").orElseThrow())
                        .receiver(userRepository.findById("user5").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 12, 12, 46, 5))
                        .sender(userRepository.findById("user4").orElseThrow())
                        .receiver(userRepository.findById("user3").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 12, 14, 46, 5))
                        .sender(userRepository.findById("user5").orElseThrow())
                        .receiver(userRepository.findById("user4").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 13, 9, 46, 5))
                        .sender(userRepository.findById("user6").orElseThrow())
                        .receiver(userRepository.findById("user5").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 14, 9, 46, 5))
                        .sender(userRepository.findById("user7").orElseThrow())
                        .receiver(userRepository.findById("user6").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 14, 16, 46, 5))
                        .sender(userRepository.findById("user2").orElseThrow())
                        .receiver(userRepository.findById("user7").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 14, 20, 46, 5))
                        .sender(userRepository.findById("user7").orElseThrow())
                        .receiver(userRepository.findById("user1").orElseThrow()).build()
        );

        friendRepository.saveAll(friends);
    }

    private void saveQuestion() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("questions.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {

            reader.lines()
                    .map(Question::new)
                    .forEach(questionRepository::save);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void saveReport() {
        User user1 = userRepository.findById("user1").orElseThrow(null);
        User user2 = userRepository.findById("user2").orElseThrow(null);
        User user3 = userRepository.findById("user3").orElseThrow(null);
        User user4 = userRepository.findById("user4").orElseThrow(null);
        User user5 = userRepository.findById("user5").orElseThrow(null);
        User user6 = userRepository.findById("user6").orElseThrow(null);
        User user7 = userRepository.findById("user7").orElseThrow(null);

        Question question1 = questionRepository.findById(1L).orElseThrow(null);
        Question question2 = questionRepository.findById(2L).orElseThrow(null);
        Question question3 = questionRepository.findById(3L).orElseThrow(null);
        Question question4 = questionRepository.findById(4L).orElseThrow(null);
        Question question5 = questionRepository.findById(5L).orElseThrow(null);

        List<Report> reports = List.of(
                Report.builder().user(user1).question(question1).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user1).question(question2).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user1).question(question3).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user1).question(question4).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user1).question(question5).written(false).createdAt(LocalDateTime.now()).build(),

                Report.builder().user(user2).question(question1).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user2).question(question2).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user2).question(question3).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user2).question(question4).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user2).question(question5).written(false).createdAt(LocalDateTime.now()).build(),

                Report.builder().user(user3).question(question1).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user3).question(question2).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user3).question(question3).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user3).question(question4).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user3).question(question5).written(false).createdAt(LocalDateTime.now()).build(),

                Report.builder().user(user4).question(question1).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user4).question(question2).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user4).question(question3).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user4).question(question4).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user4).question(question5).written(false).createdAt(LocalDateTime.now()).build(),

                Report.builder().user(user5).question(question1).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user5).question(question2).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user5).question(question3).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user5).question(question4).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user5).question(question5).written(false).createdAt(LocalDateTime.now()).build(),

                Report.builder().user(user6).question(question1).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user6).question(question2).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user6).question(question3).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user6).question(question4).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user6).question(question5).written(false).createdAt(LocalDateTime.now()).build(),

                Report.builder().user(user7).question(question1).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user7).question(question2).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user7).question(question3).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user7).question(question4).written(false).createdAt(LocalDateTime.now()).build(),
                Report.builder().user(user7).question(question5).written(false).createdAt(LocalDateTime.now()).build()
        );
        reportRepository.saveAll(reports);
    }

    private void saveStationQuestion() {
        List<Station> stations = stationRepository.findAll();
        List<Question> questions = questionRepository.findAll();
        List<StationQuestion> stationQuestions = List.of(
                StationQuestion.builder().station(stations.get(0)).question(questions.get(0)).createdAt(LocalDate.now()).build(),
                StationQuestion.builder().station(stations.get(1)).question(questions.get(1)).createdAt(LocalDate.now()).build(),
                StationQuestion.builder().station(stations.get(2)).question(questions.get(2)).createdAt(LocalDate.now()).build(),
                StationQuestion.builder().station(stations.get(3)).question(questions.get(3)).createdAt(LocalDate.now()).build(),
                StationQuestion.builder().station(stations.get(4)).question(questions.get(4)).createdAt(LocalDate.now()).build()
        );
        stationQuestionRepository.saveAll(stationQuestions);
    }

    private void saveStationReport() {
        List<Station> stations = stationRepository.findAll();
        List<Report> reports = reportRepository.findAll();

        List<StationReport> stationReports = List.of(
                StationReport.builder().station(stations.get(0)).report(reports.get(0)).build(),
                StationReport.builder().station(stations.get(0)).report(reports.get(5)).build(),
                StationReport.builder().station(stations.get(1)).report(reports.get(1)).build(),
                StationReport.builder().station(stations.get(1)).report(reports.get(6)).build(),
                StationReport.builder().station(stations.get(1)).report(reports.get(11)).build(),
                StationReport.builder().station(stations.get(2)).report(reports.get(2)).build(),
                StationReport.builder().station(stations.get(2)).report(reports.get(12)).build(),
                StationReport.builder().station(stations.get(3)).report(reports.get(3)).build(),
                StationReport.builder().station(stations.get(3)).report(reports.get(8)).build(),
                StationReport.builder().station(stations.get(4)).report(reports.get(4)).build(),
                StationReport.builder().station(stations.get(4)).report(reports.get(19)).build(),
                StationReport.builder().station(stations.get(4)).report(reports.get(24)).build(),
                StationReport.builder().station(stations.get(4)).report(reports.get(34)).build()
        );
       stationReportRepository.saveAll(stationReports);
    }

    // 초기 데이터
    private void saveSkin() {
        String[] skins = {"skin_01", "skin_02", "skin_03", "skin_04", "skin_05", "skin_06", "skin_07"};
        for (String name : skins) {
            Skin skin = new Skin();
            skin.setName(name);
            skinRepository.save(skin);
        }
    }
    private void saveExpression() {
        String[] expressions = {"face_01", "face_02", "face_03", "face_04", "face_05", "face_06", "face_07", "face_08", "face_09"};
        for (String name : expressions) {
            Expression expression = new Expression();
            expression.setName(name);
            expressionRepository.save(expression);
        }
    }
    private void saveHair() {
        String[] hairs = {"hair_01", "hair_02", "hair_03", "hair_04", "hair_05", "hair_06", "hair_07", "hair_08", "hair_09"};
        for (String name : hairs) {
            Hair hair = new Hair();
            hair.setName(name);
            hairRepository.save(hair);
        }
    }
    private void saveItem() {
        String[] items = {"acc1_01", "acc2_01", "acc3_01", "acc4_01", "acc5_01", "acc7_01", "acc1_02", "fas1_01", "fas1_02", "fas1_03", "fas4_01"};        // acc6_01이 없음
        for (String name : items) {
            String number = null;
            if (name.startsWith("acc")) { number = name.substring(name.indexOf("acc")+3, name.length()-3); }
            if (name.startsWith("fas")) { number = name.substring(name.indexOf("fas")+3, name.length()-3); }
            switch (Objects.requireNonNull(number)) {
                case "1":
                    Head head = new Head();
                    head.setName(name);
                    headRepository.save(head);
                    break;
                case "2":
                    Eyes eyes = new Eyes();
                    eyes.setName(name);
                    eyesRepository.save(eyes);
                    break;
                case "3":
                    Ears ears = new Ears();
                    ears.setName(name);
                    earsRepository.save(ears);
                    break;
                case "4":
                    Neck neck = new Neck();
                    neck.setName(name);
                    neckRepository.save(neck);
                    break;
                case "5":
                    LeftWrist leftWrist = new LeftWrist();
                    leftWrist.setName(name);
                    leftWristRepository.save(leftWrist);
                    break;
                case "6":
                    RightWrist rightWrist = new RightWrist();
                    rightWrist.setName(name);
                    rightWristRepository.save(rightWrist);
                    break;
                case "7":
                    LeftHand leftHand = new LeftHand();
                    leftHand.setName(name);
                    leftHandRepository.save(leftHand);
                    break;
                case "8":
                    RightHand rightHand = new RightHand();
                    rightHand.setName(name);
                    rightHandRepository.save(rightHand);
                    break;
                default:
                    break;
            }
        }
    }
    private void saveTop() {
        String[] tops = {"top_01", "top_02", "top_03", "top_04", "top_05", "top_06", "top_07", "top_08", "top_09"};
        for (String name : tops) {
            Top top = new Top();
            top.setName(name);
            topRepository.save(top);
        }
    }
    private void saveBottom() {
        String[] bottoms = {"bottom_01", "bottom_02", "bottom_03", "bottom_04", "bottom_05", "bottom_06", "bottom_07", "bottom_08"};
        for (String name : bottoms) {
            Bottom bottom = new Bottom();
            bottom.setName(name);
            bottomRepository.save(bottom);
        }
    }
    private void saveShoes() {
        String[] shoess = {"shoes_01", "shoes_02", "shoes_03", "shoes_04", "shoes_05", "shoes_06", "shoes_07", "shoes_08", "shoes_09"};
        for (String name : shoess) {
            Shoes shoes = new Shoes();
            shoes.setName(name);
            shoesRepository.save(shoes);
        }
    }
    private void saveStarBackground() {
        String[] bgimgs = {"background_01", "background_02", "background_03", "background_04", "background_05"};
        for (String name : bgimgs) {
            StarBackground bgimg = new StarBackground();
            bgimg.setName(name);
            starBackgroundRepository.save(bgimg);
        }
    }
    private void saveStationBackground() {
        String[] bgimgs = {"station_01", "station_02", "station_03", "station_04"};
        for (String name : bgimgs) {
            StationBackground bgimg = new StationBackground();
            bgimg.setName(name);
            stationBackgroundRepository.save(bgimg);
        }
        stationBackgroundRepository.flush();
    }
}
