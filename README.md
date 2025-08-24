## 청년의 시작, 확신으로 바꾸다. <창업할각?> - BE
<img width="900" alt="창업할각메인페이지" src="https://github.com/user-attachments/assets/20f0652d-ef72-49da-a3ab-45655cceaf18" />

> 청년 창업자를 위한 **AI 기반 맞춤형 코칭 & 지원사업 추천 플랫폼**  
> 초기 창업자의 의사결정과 지원사업 연결을 돕는 Spring Boot 기반 백엔드 서버

<br>

## 01 서비스 개요

* **문제 정의**
   * 청년 창업자는 두 가지 큰 어려움에 직면
      1. 아이디어에 대한 객관적 피드백·멘토링 기회 부족
      2. 방대한 지원사업 공고와 복잡한 절차로 인한 신청 어려움
   * 이로 인해 창업 포기·실패 사례 증가 → 개인 문제를 넘어 **지역 일자리와 혁신 감소, 지역 경제 활력 저하, 수도권-비수도권 격차 심화**로 연결

* **현장 인사이트**
   * 한성대·신구대 창업동아리 인터뷰:
      * “맞춤형 지원사업 추천 서비스가 필요하다”
      * “경험 부족으로 어디서부터 시작해야 할지 모르겠다”
   * 인스타그램 광고 실험: 도달 대비 클릭률 평균 이상 → **청년층의 창업 수요와 맞춤형 솔루션에 대한 높은 관심** 검증

* **솔루션**
   * <창업할각?>은 단순 정보 제공을 넘어 **AI 기반 맞춤형 플랫폼**으로서
      * 창업자의 준비 과정과 전략 수립 지원
      * 적합한 지원사업 매칭 및 사업계획서 첨삭 제공
      * 주간 실행 리포트를 통한 **지속적 코칭과 실행 지원** 제공
   * 결과적으로 **지역 창업 생태계 활성화와 균형 발전**에 기여

<br>


## 02 핵심 기능

- **분석할각**: 사용자 입력(역량, 자원)을 기반으로 창업 유형/전략 분석  
- **선정될각?**: 사업계획서 첨삭 + 예상 Q&A 제공 → 선정 확률 향상  
- **리포트 자동 생성**: AI가 SWOT/실행 로드맵을 생성, 각도(Angle) 수치화  
- **주간 리포트 메일 발송**: 스케줄러가 매주 최신 리포트 생성 후 이메일 전송  
- **지원사업 매칭**: AI 서버에서 BERT 임베딩 기반으로 수천 건 공고와 빠른 유사도 매칭

<br>


## 03 AI 활용

- **GPT 대비 차별성**  
  - GPT: 글자 수 제한, 많은 후보 비교에 한계  
  - 창업할각?: BERT 임베딩 + FAISS(Vector Search)구조를 활용해 모든 공고/아이디어를 벡터화 → 대량 매칭 및 고속 유사도 검색 가능
- **실질적 연결**  
  - 단순 참고용 요약이 아닌, 생성된 리포트가 서비스 실행 흐름에 직접 반영  
- **확장성**  
  - 수천~수만 건 지원사업에도 확장 가능  
  - 축적된 데이터로 산업군 트렌드 분석/전략적 의사결정 지원


<br>


## 04 기술 스택

- **Language**: Java 17+  
- **Framework**: Spring Boot 3.x, Spring Data JPA  
- **Database**: MySQL  
- **Infra**: Docker, 외부 FastAPI (AI), Mail Service  
- **Build Tool**: Gradle

<br>


## 05 아키텍처

```mermaid
flowchart TD
    User[User] -->|Idea 작성| IdeaService
    IdeaService --> ReportService

    ReportService -->|리포트 생성| GPTChatService
    ReportService -->|뉴스 검색| PplxClient
    ReportService -->|지원사업 유사도 검색·추천| FastAPI

    ReportService --> ReportDB[(Report DB)]
    IdeaService --> IdeaDB[(Idea DB)]
    UserService --> UserDB[(User DB)]

    Scheduler -->|매주 월요일 09시| ReportService
    ReportService --> MailService --> Email[Weekly Report 발송]

    DevOps[GitHub Actions + Docker] --> SpringBootApp[Spring Boot Application]
    SpringBootApp -->|실행| ReportService
````

<br>


## 06 ERD
<img width="900" alt="image" src="https://github.com/user-attachments/assets/cf37ef6b-4a61-468d-bf72-5ea1a1b96249" />


<br>


## 07 API 명세서

- https://www.notion.so/hyejinworkspace/API-2245f14ba7a580cbbb36fc97988ee575

<br>


## 08 빠른 실행 가이드
- 레포지토리 클론
- 환경변수 세팅 (application-secret.properties)
    ```bash
    ## DB(local)
    DATABASE_URL=jdbc:mysql://localhost:3306/changuphalggak
    DATABASE_USERNAME=YOUR_USER
    DATABASE_PASSWORD=YOUR_PASSWORD
    
    ## GPT
    OPENAI_KEY=YOUR_OPENAI_KEY
    
    ## SMTP
    MAIL_USERNAME=YOUR_MAIL_ID
    MAIL_PASSWORD=YOUR_MAIL_PASSWORD
    
    ## Perplexity
    PPLX_KEY=YOUR_PPLX_KEY
    ```

- 애플리케이션 실행
./gradlew clean build
./gradlew bootRun

<br>


## 09 협업 규칙

* **Commit**: `태그: 작업 설명` (예: `feat: 리포트 생성 API 추가`)
  태그: feat, fix, refactor, chore, docs, add, del, move, rename, settings
* **Branch**: main (배포) / develop (통합), feature: `feat/#이슈번호-이름`, fix: `fix/#이슈번호-이름`
* **Issue**: 간결한 제목 + 상세 내용, PR 시 `close #번호`로 닫기
* **프로젝트 관리**: GitHub Projects를 활용해 칸반 보드 기반 태스크 관리, 1주 단위 스프린트와 회고 진행 → 애자일 방법론 일부 도입

<br>


## 10 수익성 & 지속 가능성

* **수익 모델**: 무료 기본 + 프리미엄 구독
* **확장 경로**: 대학·지자체·공공기관과 연계 → 창업 클러스터 형성
* **지역 효과**: 청년 정착, 일자리 창출, 균형 발전, 사회적 기업 확산

<br>

## 11 백엔드 품질 및 기술적 완성도

* **운영 안정성**
  * GitHub Actions + Docker 기반 CI/CD 파이프라인 구축 → 자동 빌드·배포로 안정적인 서비스 운영
  * 예외 처리 및 로깅 체계 세분화 → 장애 발생 시 빠른 진단 및 복구 가능
  * @Valid 기반 요청 검증과 표준화된 예외·응답 구조 적용 → 일관된 API 품질 유지

* **아키텍처 및 코드 품질**
  * 패키지를 `domain`, `global`, `infra`로 분리 → 가독성 및 재사용성 향상
  * N+1 문제 방지를 위해 `fetch join` 및 JPA 최적화 적용
  * 로컬/운영 환경 분리 (application-dev, application-prod) → 안전한 배포 구조

* **보안 및 인프라**
  * 민감 정보는 `application-secret.properties` + `.gitignore`로 관리 → 보안 강화
  * CORS 정책 세분화 및 Nginx 리버스 프록시 적용 → 외부 접근 제어 및 확장성 확보


<br>

