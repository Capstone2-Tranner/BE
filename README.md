# Tranner - AI 기반 여행 계획 서비스 (Backend)

Spring Boot 기반 MSA 아키텍처로 구현된 여행 계획 서비스 백엔드

## 기술 스택

- **Framework**: Spring Boot 3.4, Spring Cloud
- **Language**: Java 17
- **Architecture**: MSA (Microservice Architecture)
- **Service Discovery**: Eureka Server
- **API Gateway**: Spring Cloud Gateway
- **Database**: MySQL, Redis
- **Security**: JWT, OAuth2 (Kakao, Google)
- **Build Tool**: Gradle

## 마이크로서비스 구성

### Eureka Server
- 서비스 디스커버리 및 레지스트리
- 모든 마이크로서비스 등록 및 관리

### API Gateway
- 단일 진입점 제공
- JWT 인증 및 라우팅
- 로깅 및 모니터링

### Account Service
- 회원가입/로그인 (자체, OAuth2)
- JWT 토큰 발급 및 갱신
- 여행 계획 CRUD
- 장바구니 관리
- 이메일 인증

### External API Proxy
- Google Places API 연동
- 장소 검색 및 상세정보 조회
- 인기 명소 관리
- 장소 정보 캐싱 (Redis)

## 주요 기능

### 인증 및 회원 관리
- 자체 회원가입 (이메일 인증)
- SNS 로그인 (Kakao, Google OAuth2)
- JWT 기반 인증/인가
- Refresh Token 자동 갱신

### 여행 계획
- 여행 일정 생성/조회/수정/삭제
- 일자별 세부 일정 관리
- 장소 장바구니 기능
- 내 여행 계획 목록 조회

### 장소 탐색
- Google Places API 기반 장소 검색
- 지역별 인기 명소 Top 20
- 장소 상세 정보 조회
- 근처 장소 검색

## 프로젝트 구조

```
Tranner_BE_2/
├── eureka-server/          # 서비스 디스커버리
├── api-gateway/            # API 게이트웨이
├── account-service/        # 계정 및 여행 계획 서비스
├── external-api-proxy/     # 외부 API 연동 서비스
└── infra-compose.yml       # 인프라 Docker Compose
```

## 실행 방법

### 1. 인프라 실행 (MySQL, Redis)
```bash
docker-compose -f infra-compose.yml up -d
```

### 2. 각 서비스 실행 순서
```bash
# 1. Eureka Server
cd eureka-server
./gradlew bootRun

# 2. API Gateway
cd api-gateway
./gradlew bootRun

# 3. Account Service
cd account-service
./gradlew bootRun

# 4. External API Proxy
cd external-api-proxy
./gradlew bootRun
```

### 전체 빌드
```bash
./gradlew clean build
```

## 환경 변수 설정

각 서비스의 `application.yml` 또는 `application-{profile}.yml`에서 설정:

- **Database**: MySQL 접속 정보
- **Redis**: Redis 접속 정보
- **OAuth2**: Kakao, Google Client ID/Secret
- **JWT**: Secret Key
- **Google API**: API Key
- **Email**: SMTP 설정

## API 엔드포인트

### Account Service (Port: 8081)
- `POST /api/members/signup` - 회원가입
- `POST /api/members/login` - 로그인
- `POST /api/members/email/verification` - 이메일 인증
- `GET /api/plans` - 여행 계획 목록
- `POST /api/plans` - 여행 계획 생성
- `PUT /api/plans/{id}` - 여행 계획 수정
- `DELETE /api/plans/{id}` - 여행 계획 삭제

### External API Proxy (Port: 8082)
- `GET /api/discovery/recent` - 최근 인기 명소
- `GET /api/discovery/detail` - 장소 상세정보
- `GET /api/search` - 장소 검색

## 아키텍처 특징

- **MSA 구조**: 서비스별 독립 배포 및 확장
- **Service Discovery**: Eureka를 통한 동적 서비스 등록/발견
- **API Gateway 패턴**: 단일 진입점 및 라우팅
- **JWT 인증**: Stateless 인증 방식
- **캐싱 전략**: Redis를 통한 API 응답 캐싱

## CI/CD

GitHub Actions를 통한 자동화:
- 빌드 및 테스트
- Docker 이미지 생성
- AWS EC2 배포

## 팀원

- 문상준 (20203047)
- 강민석 (20201815)
- 김민우 (20201822)
- 김현진 (20213032)
- 조은빈 (20223055)

## 라이선스

캡스톤 디자인 프로젝트
