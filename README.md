# 2-Lucy-Park-Community-BE

## 프로젝트 개요와 초기 세팅
Spring Boot는 해커톤 이후 두 번째로 접해보는 프레임워크였기에 거의 처음이라 해도 무방했다.  
자연스러운 흐름과 디테일은 자신이 없었지만, 최대한 명확한 기준을 가지고 설계를 진행했다.

### ERD 및 API 문서
- [ERD 보러가기](https://www.erdcloud.com/d/QhsY8ZhgM5Bt6a2KL)
- [REST API 명세](https://www.notion.so/lucy-park-community-rest-api-1a0b8506ffa08055b252f48a62c78132?pvs=21)

## 📦 Entity & DTO 설계 기준
- 명시적 설정과 기본 설정을 구분하고자 했고, 필드 순서에도 일관성을 두었다 (식별자 → 단순 필드 → 연관관계).
- 성능 최적화를 위해 Comment DTO에 직접 FK를 설정했고, soft delete를 위해 `CascadeType.PERSIST` 를 사용했다.
- `columnDefinition`으로 boolean을 명확히 표현하고자 했고, refreshToken과 User의 관계를 OneToOne으로 확장했다.

## 🔁 Repository와 예외 처리
- 메서드 네이밍 규칙을 따르되 복잡한 쿼리는 직접 JPQL 작성.
- `@ControllerAdvice`를 통해 전역 예외 처리
- `/error` 접근을 Security에서 허용하도록 수정하여 403 오류 해결.

## 🔐 Security와 JWT
- 비대칭키(JWT 공개키/비밀키) 설정을 환경변수로 관리하며 보안성을 높였다.
- `@Value` 주입 방식, PEM 형식 에러, jjwt 버전 호환 문제 등 다양한 시행착오를 겪으며 학습.
- `@AuthenticationPrincipal`을 사용한 리팩토링을 일부 적용하여 코드 중복을 줄이고자 시도함.

## 🛠️ 서비스 레이어 설계
- 사용자 정보 수정 관련 API는 로그 추적 및 유지보수성 측면에서 분리 처리.
- 반복 로직은 `JwtTokenProvider` 내부 로직으로 통합.
- 양방향 참조에 대한 무한루프 방지를 위해 `@JsonIdentityInfo` 도입 및 DTO로 분리하여 해결.
- `@EntityGraph`를 통한 Lazy Loading 최적화, 댓글 정렬을 위한 엔티티 정렬 설정 등 성능 개선 시도.

## 📂 컨트롤러와 요청/응답 구조
- DTO 유효성 검증을 위한 `@Valid @RequestBody` 사용.
- 공통 응답 형태를 위해 WrapperResponse 도입.

## ⚙️ FE-BE 연동에서의 시행착오
- 이미지 업로드, CORS, 최대 업로드 사이즈, API 경로 문제 등의 문제 해결함.

## ✅ 테스트 코드 설계 기준
### 테스트 대상 선정 기준
1. 예측 가능성과 외부 의존성이 모두 낮은 영역
2. 예측 가능성과 외부 의존성이 모두 높은 영역 (리팩토링 후 가성비 영역만 테스트)

### 단위 테스트
| 계층 | 디렉토리 | 메서드 | 비고    |
|------|------------|----------|-------|
| Controller | 전체 | 전체 (DTO 없는 메서드 제외) | 구현 완료 |
| Service | 전체 | 일부 로직 없는 메서드 제외 | 구현 완료 |
| Repository | Post, Comment | @Query 사용 메서드 | 구현 완료 |

### 통합 테스트
- Controller + Security + Exception + Service 흐름 통합 검증
- `@SpringBootTest`, `MockMvc` 활용

### 테스트 전략
| 계층 | 테스트 포인트 | 방식 |
|------|----------------|--------|
| Controller | 유효성, 응답, 서비스 호출 | `@WebMvcTest`, `MockMvc` |
| Service | 로직 (중복 검사, 암호화 등) | `@ExtendWith(MockitoExtension.class)` |
| Repository | 커스텀 쿼리 | `@DataJpaTest` + `EntityManager` |
| 통합 흐름 | 로그인→글쓰기→로그아웃 | `@SpringBootTest` |

### 컨트롤러 테스트에 대해 알게된 점
- `MockMvc`, `ObjectMapper`, `@MockBean`, `verify()` 등 사용법 숙지
- 기능별 파일 분리로 가독성 및 유지보수성 강화
- `@AutoConfigureMockMvc(addFilters = false)` 시 보안 필터 주의

### 서비스 테스트에 대해 알게된 점
- Mock 설정은 반드시 서비스 호출 전에
- `@Mock`, `@InjectMocks`, `@ExtendWith`
- null 등 예외 상황도 Service 단에서 재검증

### Repository 테스트에 대해 알게된 점
- H2 사용 시 예약어 이슈 조심 (user → users)
- `@DataJpaTest`, `EntityManager`, `clear()` 등 활용

## 📊 Jacoco 테스트 커버리지
![Jacoco Report](attachment:56c7bdb4-226b-481d-83b7-9ab8a361c86c:스크린샷_2025-03-26_오후_3.27.45.png)

| 패키지 | 커버리지 | 설명 및 우선순위 |
|--------|-----------|------------------|
| `service` | 59% | **1순위**: 핵심 로직 집중 영역 |
| `controller` | 46% | **2순위**: 요청/응답 흐름 테스트 필요 |
| `security` | 88% | **분기 보완 필요** |
| `exception` | 56% | 3순위: 예외 흐름 처리 강화 |
| `entity`, `dto`, `config` | 16% ~ 100% | 테스트 우선순위 낮음 또는 유지 대상 |

## 🤖 GitHub Actions 통한 커버리지 PR 등록 설정 (예정)
```yaml
- name: Comment test coverage on PR
  uses: madrapps/jacoco-report@v1.2
  with:
    title: 📝 테스트 커버리지 리포트
    paths: ${{ github.workspace }}/build/reports/jacoco/test/jacocoTestReport.xml
    token: ${{ secrets.PRIVATE_REPO_ACCESS_TOKEN }}
    min-coverage-overall: 50
    min-coverage-changed-files: 50
```

---

## 💬 회고
지속 가능한 개발을 할 수 있는 개발자가 되고 싶었다.
최대한 GPT 없이 백엔드를 직접 설계/구현하고자 시도하였다.  
공부와 실전의 접점을 실감할 수 있었고, 특히 Security와 JWT 구현은 끊임없는 디버깅의 연속이었다.  
계층적 분리, 유지보수성, 커버리지, 예외 흐름까지 고려하면서 '단순히 돌아가는 코드'보다 '유지될 수 있는 구조'에 대해 고민하게 되었다.  
앞으로는 테스트 전략을 사전에 고려하여 '테스트 주도 개발' 방식으로 전환해보는 것도 좋은 시도가 될 것 같다.

