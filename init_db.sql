-- 웹메일용 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS webmail DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- webmail DB 선택
USE webmail;

-- 주소록 테이블 생성
CREATE TABLE IF NOT EXISTS addrbook (
    email VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(255),
    category VARCHAR(255),
    created_at DATETIME,
    username VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS addrbookTest (
    email VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(255),
    category VARCHAR(255),
    created_at DATETIME,
    username VARCHAR(255) NOT NULL
);