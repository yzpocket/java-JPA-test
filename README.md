# Spring MVC Practice first!
[Java] Spring에 JPA로 DB를 다루기 위한 첫 연습장입니다!

## 🖥️ 저장소 소개
JPA의 작동 방법을 테스트하기 위한 공간입니다.

## 🕰️ 학습 기간
* 23.09.01

### 🧑‍🤝‍🧑 맴버구성
- (팀장) 김인용 - 혼자!!

### ⚙️ 개발 환경
- **MainLanguage** : `Java` - JDK 17
- **IDE** : IntelliJ IDEA Ultimate
- **Framework** : Java, Hibernate library
- **Database** : MySQL(Local)
- **SERVER** : None

## 📌 주요 기능
#### 학습한 기능
* Hibernate 라이브러리 사용해보기
    - persistence.xml으로 JDBC 연결
    - persistence-unit name="memo" <- 특히 entity의 @Table어노테이션의 "memo" 와 일치하는지 확인할 것
    - Entity 클래스에서 어노테이션 작동 확인
```
@Entity -> JPA가 관리할 수 있는 Entity 클래스 지정 //@Entity(name = "")작성안하면 기본 클래스이름 따라감.
@Table(name = "memo") // 매핑할 테이블의 이름을 지정
public class Memo {
```
```
@Id //idx로 알고 있었던 부분임. pk 속성을 가지고 있음.
@GeneratedValue(strategy = GenerationType.IDENTITY) //<-- Autoincrement 해주는것.
private Long id;
```
```
// nullable: null 허용 여부
// unique: 중복 허용 여부 (false 일때 중복 허용)
// length: 컬럼 길이 지정
@Column(name = "username", nullable = false, unique = true) //DB의 컬럼명과 아래 클래스 필드를 맵핑 하겠다는 의미
private String username;
```