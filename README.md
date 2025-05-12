# 📬 Webmail System with Apache James & Spring Boot

이 프로젝트는 **Apache James 메일 서버**와 **Spring Boot**를 기반으로 개발된 웹메일 시스템입니다. 사용자는 웹 환경에서 메일을 송수신하고 관리할 수 있으며, 관리자 기능을 통해 사용자 계정도 손쉽게 관리할 수 있습니다.

## 🔧 기술 스택

- **Backend**: Spring Boot, Java 21
- **Mail Server**: Apache James 3.8.2
- **Database**: MySQL
- **Build Tool**: Maven 
- **View**: JSP

## ✅ 테스트 & 품질 관리 도구

- **JUnit 5**: 단위 테스트
- **JaCoCo**: 테스트 커버리지 측정
- **SonarQube**: 정적 코드 분석 및 품질 리포팅

## ✨ 주요 기능

- 사용자 로그인 / 로그아웃
- 메일 송신 및 수신 (Apache James 연동)
- 받은 편지함 / 보낸 편지함
- 첨부파일 기능
- 사용자 관리 (관리자 전용):
  - 사용자 추가
  - 사용자 삭제
  - 사용자 목록 조회
