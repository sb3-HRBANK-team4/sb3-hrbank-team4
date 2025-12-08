 # <img width="48" height="48" alt="image" src="https://github.com/user-attachments/assets/e986bbbe-6f6a-4c4b-8015-e211309ff3cd" /> SB3 HRBANK Team4


## 📌 프로젝트 소개
- 기업 인사관리 시스템의 Spring 백엔드 API 구축
- 직원 정보 관리, 부서 관리, 데이터 백업 및 통계 분석 기능 제공
- 프로젝트 기간: **2025.06.03 ~ 2025.06.13**

## 🛠️ 기술 스택
- **Backend**: Spring Boot 3.x, Spring Data JPA  
- **Database**: H2 Database (개발), PostgreSQL (운영)  
- **Documentation**: Swagger / OpenAPI 3.0  
- **Collaboration & Tools**: Git, GitHub, Discord  

## 👥 팀원 구성
| 이름  | 역할                                  | Github                                    |
|-----|-------------------------------------|-------------------------------------------|
| 강  호 | 부서 관리 API     | [kangho1870](https://github.com/kangho1870) |
| 김민준 | 직원 통계 API           | [adjoon1](https://github.com/adjoon1) |
| 이지현 | 데이터 백업 관리 API | [jhlee-codes](https://github.com/jhlee-codes) |
| 조현아 | 직원 관리 API    | [hyohyo-zz](https://github.com/hyohyo-zz)   |


## 🎯 팀원별 구현 기능 상세

### 👤 김민준
- **직원 통계 API**
  - 직원 분포 분석을 위한 GET 요청 API 구현
  - 기간별 직원 수 추이 및 변화율 조회 API 구현
- **수정 이력 관리 API**
  - 직원 정보 변경 추적을 위한 로깅 시스템 구현
  - GET 요청을 사용한 변경 이력 조회 API 구현

### 👤 강호
- **부서 관리 API**
  - 부서 정보 CRUD 처리를 위한 API 엔드포인트 구현
  - `@PathVariable`을 활용한 동적 라우팅 기능 구현
- **부서별 직원 현황**
  - 부서별 소속 직원 목록 조회 API 구현
  - 부서 삭제 시 소속 직원 존재 여부 검증 로직 구현

### 👤 이지현
- **데이터 백업 관리 API**
  - 직원 데이터 변경 여부를 판단해 자동/수동 백업을 생성하는 POST API 구현
  - 백업 이력(작업자·시간·상태·파일) 조회 및 상태 업데이트 API 구현
  - 직원 데이터를 CSV 파일로 내보내는 Export API 및 실패 시 에러 로그 파일 저장 처리 구현
  - Scheduler 기반 주기적 자동 백업 및 진행 상태 조회 기능 구현
- **파일 관리 API**
  - 파일 메타 정보(DB)와 실제 파일(로컬 디스크)을 분리 저장하는 구조 설계 및 구현
  - 파일 메타 정보 ID로 실제 파일을 다운로드하는 API 구현

### 👤 조현아
- **직원 관리 API**
  - 직원 정보 CRUD 기능을 위한 RESTful API 엔드포인트 개발
  - 커서 기반 페이지네이션을 활용한 직원 목록 조회 API 구현
- **수정 이력 & 문서화**
  - 수정 이력 API 보조 구현
  - Swagger 기반 API 문서 자동화 적용
  - 주요 기능(직원/부서/이력/백업/대시보드 등) API 명세 검토 및 통합 조율
  

## 📁 파일 구조
```
.
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ com
│  │  │     └─ fource
│  │  │        └─ hrbank
│  │  │           ├─ controller
│  │  │           │  ├─ EmployeeController.java
│  │  │           │  ├─ DepartmentController.java
│  │  │           │  ├─ BackupController.java
│  │  │           │  ├─ ChangeLogController.java
│  │  │           │  └─ FileController.java
│  │  │           ├─ service
│  │  │           │  ├─ EmployeeService.java
│  │  │           │  ├─ DepartmentService.java
│  │  │           │  ├─ BackupService.java
│  │  │           │  ├─ ChangeLogService.java
│  │  │           │  └─ FileService.java
│  │  │           ├─ repository
│  │  │           │  ├─ EmployeeRepository.java
│  │  │           │  ├─ DepartmentRepository.java
│  │  │           │  ├─ BackupRepository.java
│  │  │           │  ├─ ChangeLogRepository.java
│  │  │           │  └─ FileRepository.java
│  │  │           ├─ domain
│  │  │           │  ├─ Employee.java
│  │  │           │  ├─ Department.java
│  │  │           │  ├─ BackupLog.java
│  │  │           │  ├─ ChangeLog.java
│  │  │           │  ├─ ChangeDetail.java
│  │  │           │  └─ FileEntity.java
│  │  │           ├─ dto
│  │  │           │  ├─ employee
│  │  │           │  │  ├─ EmployeeDto.java
│  │  │           │  │  ├─ EmployeeCreateDto.java
│  │  │           │  │  └─ EmployeeUpdateDto.java
│  │  │           │  ├─ department
│  │  │           │  │  ├─ DepartmentDto.java
│  │  │           │  │  └─ DepartmentCreateDto.java
│  │  │           │  ├─ backup
│  │  │           │  │  └─ BackupDto.java
│  │  │           │  ├─ changelog
│  │  │           │  │  ├─ ChangeLogDto.java
│  │  │           │  │  └─ DiffsDto.java
│  │  │           │  └─ common
│  │  │           │     ├─ ErrorResponse.java
│  │  │           │     ├─ CursorPageResponse.java
│  │  │           │     ├─ ResponseMessage.java
│  │  │           │     └─ ResponseDetails.java
│  │  │           ├─ exception
│  │  │           │  ├─ GlobalExceptionHandler.java
│  │  │           │  ├─ common
│  │  │           │  │  └─ BaseException.java
│  │  │           │  ├─ EmployeeNotFoundException.java
│  │  │           │  ├─ DepartmentNotFoundException.java
│  │  │           │  ├─ BackupInProgressException.java
│  │  │           │  ├─ FileIOException.java
│  │  │           │  └─ InvalidRequestException.java
│  │  │           ├─ config
│  │  │           │  ├─ SwaggerConfig.java
│  │  │           │  ├─ SecurityConfig.java
│  │  │           │  └─ JpaConfig.java
│  │  │           ├─ util
│  │  │           │  ├─ IpUtils.java
│  │  │           │  ├─ CsvExportUtil.java
│  │  │           │  └─ DateTimeUtil.java
│  │  │           ├─ mapper
│  │  │           │  ├─ EmployeeMapper.java
│  │  │           │  ├─ DepartmentMapper.java
│  │  │           │  └─ ChangeDetailMapper.java
│  │  │           └─ HrbankApplication.java
│  │  └─ resources
│  │     ├─ application.yml
│  │     ├─ application-dev.yml
│  │     ├─ application-prod.yml
│  │     └─ data.sql
│  └─ test
│     ├─ java
│     │  └─ com
│     │     └─ fource
│     │        └─ hrbank
│     │           ├─ service
│     │           │  ├─ EmployeeServiceTest.java
│     │           │  ├─ DepartmentServiceTest.java
│     │           │  │  └─ BackupServiceTest.java
│     │           │  ├─ controller
│     │           │  │  ├─ EmployeeControllerTest.java
│     │           │  │  └─ DepartmentControllerTest.java
│     │           │  └─ HrbankApplicationTests.java
├─ build.gradle
├─ gradlew
├─ gradlew.bat
├─ .gitignore
└─ README.md

```

## 🌟 핵심 기능
### 👤 직원 정보 · 수정 이력 관리
**직원 등록**  
![직원등록_gif](https://github.com/user-attachments/assets/98692f34-dbe0-42a8-bacc-a3f2e4d1668d)

**직원 목록 조회**  
![직원목록조회_gif](https://github.com/user-attachments/assets/b29fba7f-7973-4d16-b125-11b2cc3c4c0c)

**직원 수정 · 수정 이력 조회**  
![수정:수정이력_gif](https://github.com/user-attachments/assets/4d442636-fd91-4a67-af3f-2a537cb596af)

**직원 삭제 · 수정 이력 조회**  
![직원삭제:수정이력_gif](https://github.com/user-attachments/assets/3dbcc013-702e-4377-956d-9731517e52da)

**직원 수 변동 추이 조회**  
![직원수변동추이_gif](https://github.com/user-attachments/assets/d874d19e-65cb-4d7b-ac39-bc0b5596f66d)

### 🏢 부서 관리
**부서 등록 · 수정 · 삭제**  
![부서등록:수정:삭제_gif](https://github.com/user-attachments/assets/e55d4d42-5da2-489a-88c3-4d8b41673a76)

**부서 목록 조회**  
![부서목록조회_gif](https://github.com/user-attachments/assets/cb634f3e-bfde-4917-8132-c71486e619dc)

### 🗂️ 데이터 백업 · 파일 관리
**데이터 백업 목록 조회**  
![데이터백업목록조회_gif](https://github.com/user-attachments/assets/c9a92f03-7c5f-488f-9f7c-6c49907e08cf)

**데이터 백업 생성**  
![데이터백업생성합본_gif](https://github.com/user-attachments/assets/828bdc39-739b-4186-8fe9-4877c4d8a0df)


## Swagger API
    Swagger UI: http://localhost:8080/swagger-ui.html  (개발 서버 실행 후 접속 가능)
 
## 배포 링크
    sb3-hrbank-team4-production.up.railway.app   → 실제 API 호출 테스트 및 배포 확인 가능

## 📎 추가 자료
📘 [팀 노션](https://ohgiraffers.notion.site/4-207649136c1180699dbaebd37fd1d399?source=copy_link)  
