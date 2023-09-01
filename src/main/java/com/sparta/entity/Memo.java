package com.sparta.entity;

import jakarta.persistence.*;

@Entity //(name = "Memo") // JPA가 관리할 수 있는 Entity 클래스 지정 //@Entity(name = "")작성안하면 기본 클래스이름 따라감.
@Table(name = "memo") // 매핑할 테이블의 이름을 지정
public class Memo {
    @Id //idx로 알고 있었던 부분임. pk 속성을 가지고 있음.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //<-- Autoincrement 해주는것.
    private Long id;

    // nullable: null 허용 여부
    // unique: 중복 허용 여부 (false 일때 중복 허용)
    @Column(name = "username", nullable = false, unique = true) //DB의 컬럼명과 아래 클래스 필드를 맵핑 하겠다는 의미
    private String username;

    // length: 컬럼 길이 지정
    @Column(name = "contents", nullable = false, length = 500)
    private String contents;
}