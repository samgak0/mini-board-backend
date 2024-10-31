package shop.samgak.mini_board.user.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.user.entities.User;

/**
 * 사용자 정보를 저장하고 조회하기 위한 레포지토리 인터페이스 정의
 * JpaRepository를 상속받아 기본적인 CRUD 기능을 제공함
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 이메일을 기반으로 사용자 정보를 조회하는 메서드
     * 
     * @param email 사용자의 이메일
     * @return 해당 이메일을 가진 사용자 정보(Optional로 반환하여 존재하지 않을 수 있음)
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명을 기반으로 사용자 정보를 조회하는 메서드
     * 
     * @param username 사용자의 이름
     * @return 해당 사용자명을 가진 사용자 정보(Optional로 반환하여 존재하지 않을 수 있음)
     */
    Optional<User> findByUsername(String username);
}
