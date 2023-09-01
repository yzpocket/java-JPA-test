import com.sparta.entity.Memo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PersistenceTest {
    EntityManagerFactory emf;
    EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("memo");
        em = emf.createEntityManager();
    }

    //***DATA를 DB에 CREATE, UPDATE, DELETE는 트랜잭션 환경이 필수다
    //***READ는 트랜잭션 환경이 필수는 아니지만 권장한다.

    //em.persist(<objectname>) - 객체를 persistenceContext에 1차 저장 및 Ccmmit시 DB로 저장
    @Test
    @DisplayName("1차 캐시 : Entity 저장")
    void test1() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = new Memo();
            memo.setId(1L);
            memo.setUsername("Robbie");
            memo.setContents("1차 캐시 Entity 저장");
            //디버그로 내려다보면 persistenceContext에 해쉬맵,key:value로 value에 객체가 들어간 것을 확인 할 수 있다.
            //commit까지 내려가면 실제로 DB에 저장이 완료됨.

            em.persist(memo);

            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    //em.find() 는 READ 및 있는것을 persistenceContext에 1차 저장(DB로부터 불러옴) 및 Commit에 반환함.
    @Test
    @DisplayName("Entity 조회 : 캐시 저장소에 해당하는 Id가 존재하지 않은 경우")
    void test2() {
        try {
                    // em.find( 객체의 Entity 타입(=클래스), pk값)으로 실행
            Memo memo = em.find(Memo.class, 1);
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    //em.find() 예제 2 - 객체 2개를 불러오는 경우,
    @Test
    @DisplayName("Entity 조회 : 캐시 저장소에 해당하는 Id가 존재하는 경우")
    void test3() {
        try {

            Memo memo1 = em.find(Memo.class, 1);
            System.out.println("memo1 조회 후 캐시 저장소에 저장\n");

            Memo memo2 = em.find(Memo.class, 1);
            System.out.println("memo2.getId() = " + memo2.getId());
            System.out.println("memo2.getUsername() = " + memo2.getUsername());
            System.out.println("memo2.getContents() = " + memo2.getContents());


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    //em.find() 객체 동일성 보장 테스트 - row1개가 1개의 객체인것을 보장한다는것.
    @Test
    @DisplayName("객체 동일성 보장")
    void test4() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            Memo memo3 = new Memo();
            memo3.setId(2L);
            memo3.setUsername("Robbert");
            memo3.setContents("객체 동일성 보장");
            em.persist(memo3);

            Memo memo1 = em.find(Memo.class, 1);
            Memo memo2 = em.find(Memo.class, 1);
            Memo memo  = em.find(Memo.class, 2);

            System.out.println(memo1 == memo2);
            // true 객체는 새로 생성했어도(참조 주소가 달라서 Java에선 다른것으로 치지만)
            // DB에서는 같은 pk로 생성된 row데이터다.
            // Java에서는 참조형변수(객체)를 만들면 서로 다르다고 나오는데
            // EM이 찾아오는것은 같은 데이터는 같은 데이터라는 유일성을 보장한다는 것을 이해하고있어야 한다.
            System.out.println(memo1 == memo);
            // false pk가 다른것이기 때문에 둘은 다른 row다

            et.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    // em.remove() 삭제기능
    @Test
    @DisplayName("Entity 삭제")
    void test5() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = em.find(Memo.class, 2);

            em.remove(memo);

            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    @Test
    @DisplayName("쓰기 지연 저장소 확인")
    void test6() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            Memo memo = new Memo();
            memo.setId(2L);
            memo.setUsername("Robbert");
            memo.setContents("쓰기 지연 저장소");
            em.persist(memo);

            Memo memo2 = new Memo();
            memo2.setId(3L);
            memo2.setUsername("Bob");
            memo2.setContents("과연 저장을 잘 하고 있을까?");
            em.persist(memo2);

            System.out.println("트랜잭션 commit 전");
            et.commit();
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    @Test
    @DisplayName("flush() 메서드 확인")
    void test7() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            Memo memo = new Memo();
            memo.setId(4L);
            memo.setUsername("Flush");
            memo.setContents("Flush() 메서드 호출");
            em.persist(memo);

            System.out.println("flush() 전");
            em.flush(); // flush() 직접 호출 -> 커밋 전에 첫번째꺼가 바로 호출되야 한다.
            System.out.println("flush() 후\n");


            System.out.println("트랜잭션 commit 전");
            et.commit();
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    //UPDATE는 어떻게 처리하는지
    //TRANSACTION START -> READ -> THIS>LOADEDSTATE 최초상태저장 -> FLUSH -> 현재 상태 ENTITY INSTANCE DB로부터 불러와서 저장함 -> 두개를 비교 -> 변경이 되었네? 감지함 -> 쓰기 저장소 ACTION QUEUE에 UPDATE문 저장 -> SET으로 ENTITY객체에 변경 값을 할당 -> COMMIT 실행으로 변경된 객체를 변경함
    //TRANSACTION이 필수적으로 진행 되어야 위와 같은 과정이 진행됨.
    @Test
    @DisplayName("변경 감지 확인")
    void test8() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            System.out.println("변경할 데이터를 조회합니다.");
            Memo memo = em.find(Memo.class, 4);
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());

            System.out.println("\n수정을 진행합니다.");
            memo.setUsername("Update");
            memo.setContents("변경 감지 확인");

            System.out.println("트랜잭션 commit 전");
            et.commit();
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    //영속성 persistenceContext의 기능
    //1. 1차 캐시 기능 맵자료구조로 되어있고 효율적으로 관리되고 있다.
    //2. 쓰기저장지연소 Action Queue 트랜잭션처럼 쿼리를 담고 있다가 Commit하면서 한번에 반영을 해줌
    //3. 변경 감지 기능. 최초상태를 저장하고 현재 상태를 불러와서 비교(Flush시점에 비교)하여 변경점이 있으면 Action Queue에 Update를 저장하고 Commit 후 반영해줌
}

