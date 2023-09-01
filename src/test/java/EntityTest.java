import com.sparta.entity.Memo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EntityTest {
    //트랜잭션은 DB데이터들의 무결성과 정합성을 유지하기 위한 논리적 개념
    //SQL중 단 하나라도 실패하면 모든 변경을 되돌리는것(커밋을 해야 DB에 반영 됨)
    //JPA는 이런 영속성 컨텍스트로 관리하는 개념을 도입했다.

    EntityManagerFactory emf;
    EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("memo");
        em = emf.createEntityManager();
    }

    @Test
    @DisplayName("EntityTransaction 성공 테스트")
    void test1() {
        EntityTransaction et = em.getTransaction(); // [1] EntityManager 에서 EntityTransaction 을 가져옵니다.

        et.begin(); // [2] 트랜잭션을 시작합니다.
        //영속성 컨텍스트(임시저장소같은느낌) 시작. -> action queue인 Commit 전까지 임시보호소 대기
        try { // DB 작업을 수행합니다.

            Memo memo = new Memo(); // 저장할 Entity 객체를 생성합니다.
            memo.setId(1L); // 식별자 값을 넣어줍니다.
            memo.setUsername("Robbie");
            memo.setContents("영속성 컨텍스트와 트랜잭션 이해하기");

            em.persist(memo); // EntityManager 사용하여 memo 객체를 영속성 컨텍스트에 저장합니다.
            //1차 캐시 저장.

            et.commit(); // 오류가 발생하지 않고 정상적으로 수행되었다면 commit 을 호출합니다.
            // commit 이 호출되면서 DB 에 수행한 DB 작업들이 반영됩니다.
        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback(); // DB 작업 중 오류 발생 시 rollback 을 호출합니다.
        } finally {
            em.close(); // 사용한 EntityManager 를 종료합니다.
        }

        emf.close(); // 사용한 EntityManagerFactory 를 종료합니다.
    }
}