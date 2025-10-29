 # <img width="48" height="48" alt="image" src="https://github.com/user-attachments/assets/e986bbbe-6f6a-4c4b-8015-e211309ff3cd" /> SB3 HRBANK Team4

 ### 프로젝트 소개
- 기업 인사관리 시스템의 Spring 백엔드 API 구축
- 직원 정보 관리, 부서 관리, 데이터 백업 및 통계 분석 기능 제공
- 프로젝트 기간: 2025.06.03 ~ 2025.06.13
#### 🛠️ 기술 스택
      Backend: Spring Boot 3.x, Spring Security, Spring Data JPA
      Database: H2 Database (개발), PostgreSQL (운영)
      Documentation: Swagger/OpenAPI 3.0
      공통 Tool: Git & Github, Discord
    
###    👥 팀원 구성
| 이름  | 역할                                  | Github                                    |
|-----|-------------------------------------|-------------------------------------------|
| 강  호 | 부서 관리 API     | [kangho1870](https://github.com/kangho1870) |
| 김민준 | 직원 통계 API           | [adjoon1](https://github.com/adjoon1) |
| 이지현 | 데이터 백업 관리 API | [jhlee-codes](https://github.com/jhlee-codes) |
| 조현아 | 직원 관리 API    | [hyohyo-zz](https://github.com/hyohyo-zz)   |


---
## 🎯 팀원별 구현 기능 상세
#### 김민준
    직원 통계 API
      - 직원 분포 분석을 위한 GET 요청 API 구현
      - 직원 수 추이 분석 API (기간별 변화율 포함)
      - 수정 이력 관리 API
      - 직원 정보 변경 추적을 위한 로깅 시스템 구현
      - GET 요청을 사용한 변경 이력 조회 API 개발
    
#### 강호
    부서 관리 API
      - 부서 정보 CRUD 처리를 위한 API 엔드포인트 구현
      - @PathVariable을 사용한 동적 라우팅 기능 구현
      - 부서별 직원 현황 API
      - 부서별 소속 직원 목록 조회 API 개발
      - 부서 삭제 시 소속 직원 존재 여부 검증 로직 구현
    
    
#### 이지현
    데이터 백업 관리 API
      - 자동/수동 백업 생성을 위한 POST 요청 API 구현
      - 백업 이력 조회 및 상태 관리 API 엔드포인트 개발
      - CSV 데이터 내보내기
      - 직원 정보를 CSV 형식으로 내보내는 API 구현
      - 백업 진행 상태 모니터링 기능 구현
    
    
#### 조현아
    직원 관리 API
      - 직원 정보 CRUD 기능을 위한 RESTful API 엔드포인트 개발
      - 커서 기반 페이지네이션을 활용한 직원 목록 조회 API 구현
      - 수정 이력 API 보조 구현
      - Swagger 기반 API 문서 자동화 적용
      - 주요 기능(직원/부서/이력/백업/대시보드 등) API 명세 검토 및 통합 조율

---

### 📁 파일 구조
    src
     ┣ main
     ┃ ┣ java
     ┃ ┃ ┣ com
     ┃ ┃ ┃ ┣ fource
     ┃ ┃ ┃ ┃ ┣ hrbank
     ┃ ┃ ┃ ┃ ┃ ┣ controller
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeController.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ DepartmentController.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ BackupController.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ ChangeLogController.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ FileController.java
     ┃ ┃ ┃ ┃ ┃ ┣ service
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeService.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ DepartmentService.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ BackupService.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ ChangeLogService.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ FileService.java
     ┃ ┃ ┃ ┃ ┃ ┣ repository
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeRepository.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ DepartmentRepository.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ BackupRepository.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ ChangeLogRepository.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ FileRepository.java
     ┃ ┃ ┃ ┃ ┃ ┣ domain
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ Employee.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ Department.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ BackupLog.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ ChangeLog.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ ChangeDetail.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ FileEntity.java
     ┃ ┃ ┃ ┃ ┃ ┣ dto
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ employee
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeDto.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeCreateDto.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ EmployeeUpdateDto.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ department
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ DepartmentDto.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ DepartmentCreateDto.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ backup
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ BackupDto.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ changelog
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ ChangeLogDto.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ DiffsDto.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ common
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ ErrorResponse.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ CursorPageResponse.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ ResponseMessage.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ ResponseDetails.java
     ┃ ┃ ┃ ┃ ┃ ┣ exception
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ GlobalExceptionHandler.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ common
     ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ BaseException.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeNotFoundException.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ DepartmentNotFoundException.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ BackupInProgressException.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ FileIOException.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ InvalidRequestException.java
     ┃ ┃ ┃ ┃ ┃ ┣ config
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ SwaggerConfig.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ SecurityConfig.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ JpaConfig.java
     ┃ ┃ ┃ ┃ ┃ ┣ util
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ IpUtils.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ CsvExportUtil.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ DateTimeUtil.java
     ┃ ┃ ┃ ┃ ┃ ┣ mapper
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeMapper.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ DepartmentMapper.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ ChangeDetailMapper.java
     ┃ ┃ ┃ ┃ ┃ ┗ HrbankApplication.java
     ┃ ┣ resources
     ┃ ┃ ┣ application.yml
     ┃ ┃ ┣ application-dev.yml
     ┃ ┃ ┣ application-prod.yml
     ┃ ┃ ┗ data.sql
     ┣ test
     ┃ ┣ java
     ┃ ┃ ┣ com
     ┃ ┃ ┃ ┣ fource
     ┃ ┃ ┃ ┃ ┣ hrbank
     ┃ ┃ ┃ ┃ ┃ ┣ service
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeServiceTest.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ DepartmentServiceTest.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ BackupServiceTest.java
     ┃ ┃ ┃ ┃ ┃ ┣ controller
     ┃ ┃ ┃ ┃ ┃ ┃ ┣ EmployeeControllerTest.java
     ┃ ┃ ┃ ┃ ┃ ┃ ┗ DepartmentControllerTest.java
     ┃ ┃ ┃ ┃ ┃ ┗ HrbankApplicationTests.java
     ┣ build.gradle
     ┣ gradlew
     ┣ gradlew.bat
     ┣ .gitignore
     ┗ README.md
    
### Swagger API
    Swagger UI: http://localhost:8080/swagger-ui.html  (개발 서버 실행 후 접속 가능)
    
### 배포 링크
    sb3-hrbank-team4-production.up.railway.app   → 실제 API 호출 테스트 및 배포 확인 가능
