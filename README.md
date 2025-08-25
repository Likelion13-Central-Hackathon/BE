## [🔗](https://likelion13-changuphalgak.netlify.app) 청년의 시작, 확신으로 바꾸다. [창업할각?] - BE
<img width="900" alt="창업할각메인페이지" src="https://github.com/user-attachments/assets/20f0652d-ef72-49da-a3ab-45655cceaf18" />

> 청년 창업자를 위한 **AI 기반 맞춤형 코칭 & 지원사업 추천 플랫폼**  
> 초기 창업자의 의사결정과 지원사업 연결을 돕는 Spring Boot 기반 백엔드 서버
<br>

## 01 Demo Screenshots

### 1. 분석할각? - 아이디어 입력 화면
아래 화면은 사용자가 창업 아이디어(예: “지역 기반 교육 스타트업”)를 입력하는 초기 화면입니다.
어떤 분야에서 창업을 고려하고 있는지, 어떤 형태의 지원이 필요한지, 사용자의 창업 업력, 현황, 팀원, 사용가능 자본 규모, 활용가능 자원 준비중인 아이템 등에 대해 상세하게 입력받습니다.
<img width="900" alt="image" src="https://github.com/user-attachments/assets/a65f6e30-d529-469e-897b-64c12e503841" />

모든 항목을 입력하고 나면, 아래 사진과 같이 로딩페이지가 나오며 AI가 사용자의 입력 내용에 맞게 분석합니다.
<img width="900" alt="image" src="https://github.com/user-attachments/assets/3929cb99-d74d-4906-9adb-a542e318d03b" />

### 2. 분석할각? - AI 추천 레포트 조회
아래 화면은 AI가 입력된 아이디어와 해당 지역과 유사한 지원사업을 의미 유사도 기반으로 분석하여, Top-3 결과를 추천한 화면입니다.
추천된 항목에는 지원기관, 지역, 마감일, 매칭 점수가 함께 표시됩니다.
AI기반으로 분석한 강점, 약점, 기회, 위협 SWOT분석 결과와 시장적합성, 비즈니스 모델 검증, 브랜드 구축, 성장가능성을 조언해주는 레포트를 생성합니다.
해당 레포트는 PDF 버튼을 누르면 PDF저장이 가능하며, Mail버튼을 누르고 email/password 입력시 매주 업데이트되는 리포트를 사용자 이메일로 받아 볼 수 있습니다.
<img width="703" height="771" alt="image" src="https://github.com/user-attachments/assets/2c894b96-1834-45de-b22d-3073e87ee58d" />

### 3. 선정될각? - 사업계획서 첨삭 받기
아래 사진은 선정될각? 페이지에서 사용자의 사업계획서 첨삭을 도와준 페이지 입니다.
사업계획서는 총 5개의 문항으로 되어있으며, 각 단계별로 항목에 적합한 내용으로 첨삭을 진행해줍니다. 답변 입력하기 부분에 사용자가 첨삭받을 내용을 입력하면, AI가 해당 내용을 분석하여 지원사업에 붙을 수 있도록 사업계획서에 맞게 첨삭을 도와줍니다.
오른쪽 문항별 질의응답 예상 질문에 생성하기 버튼을 누르면 사용자 답변을 보고 심사 시 실제로 나올 수 있는 문항별 예상질문을 생성해 줍니다.
<img width="900" alt="image" src="https://github.com/user-attachments/assets/aa008b2c-c3b9-4937-a080-3d1d1dd71c90" />

### 추천 지원사업 선정 방법
AI 서버(임베딩/FAISS/성능)는 **AI 레포 README**를 참고해 주세요.

추천 점수는 “유사도 순위 기반 점수 + 규칙 가중치”를 합산해 산출합니다.
지역 맥락은 공고 지역과 아이디어 주소(시/구) \*\*직접 매칭(전국=1.0, 일치=1.0, 불일치=0.6, 미기재=0.45)\*\*으로 반영하고, 공고 대상 텍스트에 **지역명이 명시**되면 최대 **+0.10**을 추가 가점합니다.
대상 주제/분야는 공고의 `target/supportArea`와 아이디어의 설명·관심분야 **토큰 겹침**으로 최대 **+0.12** 보너스를 주고, **예비/초기/청년/학생/캠퍼스** 및 아이디어 `interestArea` 포함 시 가점을 더합니다.
그 밖에 **업력 요건 미충족은 제외**, 충족 시 **+0.03\~0.06**, **연령 적합도**(범위 내 1.0/인접 0.7/그 외 0.1/정보없음 0.2/파싱실패 0.6)를 반영하며, 최종 Top‑K를 뽑아 **45\~98** 구간으로 정규화하고 GPT로 2\~3문장 설명 사유를 생성해 제공합니다.

- 최종 점수 공식
  ```
  FinalScore =
    0.60 * basePos(유사도 순위)
  + 0.22 * region(지역일치/불일치/미기재)
  + 0.10 * age(연령 적합도)
  + 0.05 * target(대상 키워드 적합도)
  + cityBn(지역명 명시 보너스, ≤0.10)
  + kwBn(토큰 겹침 보너스, ≤0.12)
  + bizBn(업력 strong/normal 보너스, 0.06/0.03)
  ```

## 02 API 응답 예시
### 1. 최신 레포트 상세 조회
```
GET /api/reports/{reportId}
```
**Request**
```
curl -X GET "https://43.202.138.216.nip.io/api/reports/1"
```
**Response**
```
{
  "isSuccess": true,
  "code": "SUCCESS_200",
  "httpStatus": 200,
  "message": "호출에 성공하였습니다.",
  "data": {
    "id": 1,
    "researchMethod": "타겟 고객 인터뷰 및 경쟁제품 벤치마킹",
    "strength": "팀의 기술 역량이 높음",
    "weakness": "시장 인지도 부족",
    "expectedEffect": "초기 전환율 5%p 개선"
  },
  "timeStamp": "2025-08-25 19:45:00"
}
```
  
### 2. 추천 창업 지원사업 조회
```
GET /api/reports/{reportId}/recommendations
```
**Request**
```
curl -X GET "https://43.202.138.216.nip.io/api/reports/1/recommendations"
```
**Reponse**
```
{
  "isSuccess": true,
  "code": "SUCCESS_200",
  "httpStatus": 200,
  "message": "호출에 성공하였습니다.",
  "data": [
    {
      "suitability": 96,
      "supportArea": "창업교육",
      "agency": "서울과학기술대학교 / 창업지원단",
      "title": "레이저커팅기 장비교육",
      "region": "전국",
      "isRecruiting": false
    }
  ],
  "timeStamp": "2025-08-25 19:45:00"
}
```

### 3. AI 첨삭 답변 생성
```
POST /api/answers
```
**Request**
```
curl -X POST "https://43.202.138.216.nip.io/api/answers" \
-H "Content-Type: application/json" \
-d '{
  "questionNumber": 1,
  "userAnswer": "저희 창업 아이템은 AI 기반 맞춤형 학습 코칭 플랫폼입니다. 사용자의 학습 스타일과 목표를 분석하여 개인별 학습 로드맵을 자동 생성하고, 실시간으로 진도와 성취도를 추적할 수 있는 기능을 제공합니다. 예를 들어, 수험생은 시험 일정과 목표 점수를 입력하면 AI가 최적의 학습 스케줄과 교재를 추천하고, 진행 상황에 따라 보완이 필요한 영역을 분석하여 문제를 제공합니다. 주요 타겟 고객은 대학 입시 준비생과 자격증 취득을 목표로 하는 직장인입니다.""
}'
```

**Response**
```
{
  "isSuccess": true,
  "code": "SUCCESS_200",
  "httpStatus": 200,
  "message": "호출에 성공하였습니다.",
  "data": {
    "aiAnswer": "저희 창업 아이템은 AI 기반 맞춤형 학습 코칭 플랫폼으로, 사용자의 학습 스타일과 목표를 분석해 개인별 학습 로드맵을 자동 생성하고 실시간으로 진도와 성취도를 추적하는 기능을 제공합니다. 이 플랫폼은 기존 교육 서비스와 차별화된 AI 알고리즘을 통해, 수험생이 시험 일정과 목표 점수를 입력하면 최적의 학습 스케줄과 적합한 교재를 추천합니다. 또한, 학습 진행 상황을 실시간으로 모니터링하여 보완이 필요한 영역을 분석하고 적절한 문제를 제시함으로써 학습의 효율성을 극대화합니다. 시장성 측면에서, 대학 입시 준비생과 자격증 취득을 목표로 하는 직장인을 주요 타겟 고객으로 설정하여, 이들이 갖는 목표 지향적인 학습 니즈를 충족시킬 수 있습니다. 이러한 기능은 고객의 학습 효율을 높이며, 기존의 일률적인 학습법과는 다른 개인 맞춤형 학습 경험을 제공합니다. 성공적인 실행을 위해 사용자 데이터 분석과 AI 알고리즘의 지속적인 개선을 계획하고 있으며, 초기 사용자의 피드백을 통해 플랫폼을 더욱 발전시킬 예정입니다. 해당 타겟 시장의 성장률과 온라인 학습 플랫폼의 수요 증가 추세를 근거로 하여, 본 플랫폼의 시장 잠재력은 매우 높다고 판단됩니다.",
    "answerId": 10
  },
  "timeStamp": "2025-08-25 19:45:00"
}
```

- API 명세서 전문: https://www.notion.so/hyejinworkspace/API-2245f14ba7a580cbbb36fc97988ee575


## 03 기능 구조도
<img width="3712" height="1908" alt="image" src="https://github.com/user-attachments/assets/e71f610e-ce0c-42d7-95f3-39560bfcdfe4" />


<br>

## 04 서비스 개요

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


## 05 핵심 기능

- **분석할각**: 사용자 입력(역량, 자원)을 기반으로 창업 유형/전략 분석  
- **선정될각?**: 사업계획서 첨삭 + 예상 Q&A 제공 → 선정 확률 향상  
- **리포트 자동 생성**: AI가 SWOT/실행 로드맵을 생성, 각도(Angle) 수치화  
- **주간 리포트 메일 발송**: 스케줄러가 매주 최신 리포트 생성 후 이메일 전송  
- **지원사업 매칭**: AI 서버에서 BERT 임베딩 기반으로 수천 건 공고와 빠른 유사도 매칭

<br>



## 06 기술 스택

| Tag                    | 기술명                                                                                                            | 설명                                        |
| ---------------------- | -------------------------------------------------------------------------------------------------------------- | ----------------------------------------- |
| **Language**           | ![Java](https://img.shields.io/badge/Java%2017-ED8B00?logo=openjdk\&logoColor=white)                           | 최신 LTS 버전 사용, 안정성과 성능 보장                  |
|                        | ![Lombok](https://img.shields.io/badge/Lombok-CA4245?logo=apache\&logoColor=white)                             | Getter/Setter/Builder 자동 생성으로 보일러플레이트 최소화 |
| **Framework**          | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.4.x-6DB33F?logo=springboot\&logoColor=white)     | 최신 Spring 기반 애플리케이션 프레임워크                 |
|                      | ![Spring Web](https://img.shields.io/badge/Spring%20Web-6DB33F?logo=spring\&logoColor=white)                   | REST API 개발을 위한 MVC 구조 지원                 |
|                       | ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-59666C?logo=hibernate\&logoColor=white)    | ORM 기반 데이터 접근 계층 구현                       |
|                       | ![Spring Validation](https://img.shields.io/badge/Validation-6DB33F?logo=spring\&logoColor=white)              | 요청 데이터 유효성 검증                             |
|                       | ![Spring Mail](https://img.shields.io/badge/Spring%20Mail-6DB33F?logo=gmail\&logoColor=white)                  | SMTP 기반 메일 발송 지원                          |
|                       | ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity\&logoColor=white) | 인증/인가 및 보안 구조 제공                          |
| **Database**           | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql\&logoColor=white)                                | 운영 환경 RDBMS                               |
|                       | ![H2](https://img.shields.io/badge/H2%20Database-0078D4?logo=databricks\&logoColor=white)                      | 로컬·테스트 환경용 인메모리 DB                        |
| **Infra / DevOps**     | ![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker\&logoColor=white)                             | 컨테이너 기반 배포 환경                             |
|                       | ![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?logo=amazonec2\&logoColor=white)                      | 클라우드 서버 배포 및 운영                           |
|                       | ![Nginx](https://img.shields.io/badge/Nginx-009639?logo=nginx\&logoColor=white)                                | 리버스 프록시 및 로드밸런싱                           |
|                       | ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions\&logoColor=white)    | CI/CD 자동화 파이프라인                           |
| **External Services**  | ![FastAPI](https://img.shields.io/badge/FastAPI-009688?logo=fastapi\&logoColor=white)                          | 외부 AI 모델 추론 서버                            |
|                       | ![OpenAI](https://img.shields.io/badge/OpenAI-412991?logo=openai\&logoColor=white)                             | LLM 기반 AI 기능 연동                           |
|                       | ![Perplexity](https://img.shields.io/badge/Perplexity-1C1C1C?logo=perplexity\&logoColor=white)                 | 외부 검색/추론 연동                               |
|                       | ![Mail Service](https://img.shields.io/badge/Mail%20Service-0078D4?logo=minutemailer\&logoColor=white)         | 사용자 알림 메일 발송                              |
| **Build & Dependency** | ![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle\&logoColor=white)                             | 빌드 및 의존성 관리                               |
|                       | ![Spring Dependency Management](https://img.shields.io/badge/Spring%20Dependency%20Management-6DB33F?logo=spring&logoColor=white)          | 의존성 버전 일관성 유지                             |
| **Networking**         | ![HttpClient5](https://img.shields.io/badge/Apache%20HttpClient5-0A0A0A?logo=apache\&logoColor=white)          | 외부 API와의 HTTP 통신                          |
| **Test / QA**          | ![JUnit5](https://img.shields.io/badge/JUnit5-25A162?logo=junit5\&logoColor=white)                             | 단위 테스트 프레임워크                              |
|                       | ![Mockito](https://img.shields.io/badge/Mockito-2C9D3A?logo=java\&logoColor=white)                             | Mock 객체 기반 단위 테스트                         |
|                       | ![Spring Boot Starter Test](https://img.shields.io/badge/Spring%20Boot%20Starter%20Test-6DB33F?logo=spring&logoColor=white)  | 통합 테스트 환경                                 |

<br>




## 07 ERD
<img width="900" alt="image" src="https://github.com/user-attachments/assets/64142823-a70b-4eaa-b992-d707cbfe298d" />


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

## 11 백엔드 품질 & 기술적 완성도

* **운영 안정성**
  * GitHub Actions + Docker 기반 CI/CD 파이프라인 구축 → 자동 빌드·배포로 안정적인 서비스 운영
  * 예외 처리 및 로깅 체계 세분화 → 장애 발생 시 빠른 진단 및 복구 가능
  * @Valid 기반 요청 검증과 표준화된 예외·응답 구조 적용 → 일관된 API 품질 유지
  * JUnit 기반 단위/통합 테스트 점진적 확장 → 배포 전 안정성 검증

* **아키텍처 및 코드 품질**
  * 패키지를 `domain`, `global`, `infra`로 분리 → 가독성 및 재사용성 향상
  * N+1 문제 방지를 위해 `fetch join` 및 JPA 최적화 적용
  * 로컬/운영 환경 분리 (application-dev, application-prod) → 안전한 배포 구조

* **보안 및 인프라**
  * 민감 정보는 `application-secret.properties` + `.gitignore`로 관리 → 보안 강화
  * CORS 정책 세분화 및 Nginx 리버스 프록시 적용 → 외부 접근 제어 및 확장성 확보

<br>

> 본 서비스는 청년 스타트업 대표 및 지역 창업동아리 인터뷰를 기반으로 설계되었으며, 초기 사용자 피드백을 통해 개선되었습니다.
