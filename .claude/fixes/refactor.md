# 리팩토링 목록

## [SCHED-1] 스케줄러 스레드풀 설정
- **파일:** `ReportNoticeScheduler.java`
- **문제:** Spring 기본 스케줄러는 단일 스레드. 스테이션/멤버 수가 많아지면 FCM 외부 I/O로 인해 실행 시간이 길어지고 다음 실행이 밀릴 수 있음
- **수정 방향:** 스케줄러 전용 스레드풀 설정
  ```java
  @Bean
  public TaskScheduler taskScheduler() {
      ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
      scheduler.setPoolSize(3);
      return scheduler;
  }
  ```
- **우선순위:** 낮음 (스테이션 수가 충분히 많아질 때 적용)
