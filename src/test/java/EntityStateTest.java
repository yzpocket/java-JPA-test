import com.sparta.entity.Memo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EntityStateTest {
    EntityManagerFactory emf;
    EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("memo");
        em = emf.createEntityManager();
    }

    @Test
    @DisplayName("비영속과 영속 상태")
    void test1() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = new Memo(); // 비영속 상태
            memo.setId(1L);
            memo.setUsername("Robbie");
            memo.setContents("비영속과 영속 상태");

            em.persist(memo); // persist()에 객체를 넣으면 그 객체는 persistenceContext에 담기면서 영속상태(MANAGED) 가 된다.

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
    @DisplayName("준영속 상태 : detach()")
    void test2() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = em.find(Memo.class, 1);
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());

            // em.contains(entity) : Entity 객체가 현재 영속성 컨텍스트에 저장되어 관리되는 상태인지 확인하는 메서드
            System.out.println("em.contains(memo) = " + em.contains(memo));

            System.out.println("detach() 호출");
            em.detach(memo);
            System.out.println("em.contains(memo) = " + em.contains(memo)); //관리되고있는가? false cuz detach로 분리했으니까.

            System.out.println("memo Entity 객체 수정 시도");
            memo.setUsername("Update");
            memo.setContents("memo Entity Update");
            //Detach로 분리되어서 업데이트가 진행되지 않는다.
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
    @DisplayName("준영속 상태 : clear()")
    void test3() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo1 = em.find(Memo.class, 1);
            Memo memo2 = em.find(Memo.class, 2);

            // em.contains(entity) : Entity 객체가 현재 영속성 컨텍스트에 저장되어 관리되는 상태인지 확인하는 메서드
            System.out.println("em.contains(memo1) = " + em.contains(memo1));
            System.out.println("em.contains(memo2) = " + em.contains(memo2));

            System.out.println("clear() 호출");
            em.clear(); // 영속성 상태인것을 모두 분리하지만 준영속상태로 다시 불러 올 수 있는 상태로만 만든다.
            System.out.println("em.contains(memo1) = " + em.contains(memo1));
            System.out.println("em.contains(memo2) = " + em.contains(memo2));

            System.out.println("memo#1 Entity 다시 조회");
            Memo memo = em.find(Memo.class, 1);
            System.out.println("em.contains(memo) = " + em.contains(memo));
            System.out.println("\n memo Entity 수정 시도");
            memo.setUsername("Update");
            memo.setContents("memo Entity Update");

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
    @DisplayName("준영속 상태 : close()")
    void test4() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo1 = em.find(Memo.class, 1);
            Memo memo2 = em.find(Memo.class, 2);

            // em.contains(entity) : Entity 객체가 현재 영속성 컨텍스트에 저장되어 관리되는 상태인지 확인하는 메서드
            System.out.println("em.contains(memo1) = " + em.contains(memo1));
            System.out.println("em.contains(memo2) = " + em.contains(memo2));

            System.out.println("close() 호출");
            em.close(); // 이건 영속상태를 비영속상태로 완전히 풀어버린다. 따라서 바로 닫혔으니 또 부르려니 오류 발생
            Memo memo = em.find(Memo.class, 2); // Session/EntityManager is closed 메시지와 함께 오류 발생
            System.out.println("memo.getId() = " + memo.getId());

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    @Test
    @DisplayName("merge() : 저장")
    void test5() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = new Memo();
            memo.setId(3L);
            memo.setUsername("merge()");
            memo.setContents("merge() 저장");

            System.out.println("merge() 호출");
            Memo mergedMemo = em.merge(memo); //이건 DB를 조회하고 없으면 insert를 해버림, 그리고 mergedMemo에 반환(할당)해버림

            System.out.println("em.contains(memo) = " + em.contains(memo));
            System.out.println("em.contains(mergedMemo) = " + em.contains(mergedMemo));

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
    @DisplayName("merge() : 수정")
    void test6() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = em.find(Memo.class, 3);
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());

            System.out.println("em.contains(memo) = " + em.contains(memo));

            System.out.println("detach() 호출");
            em.detach(memo); // 준영속 상태로 전환
            System.out.println("em.contains(memo) = " + em.contains(memo));

            System.out.println("준영속 memo 값 수정");
            memo.setContents("merge() 수정");

            System.out.println("\n merge() 호출");
            Memo mergedMemo = em.merge(memo);
            System.out.println("mergedMemo.getContents() = " + mergedMemo.getContents());

            System.out.println("em.contains(memo) = " + em.contains(memo));
            System.out.println("em.contains(mergedMemo) = " + em.contains(mergedMemo));

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
}
