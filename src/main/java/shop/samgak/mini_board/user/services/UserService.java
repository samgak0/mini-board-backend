package shop.samgak.mini_board.user.services;

import java.util.List;

import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * 사용자 관련 비즈니스 로직을 정의하는 인터페이스
 */
public interface UserService {
    /**
     * 모든 사용자 정보를 가져오는 메서드
     *
     * @return 모든 사용자의 UserDTO 목록
     */
    List<UserDTO> getAll();

    /**
     * 새로운 사용자를 저장하는 메서드
     *
     * @param username 사용자 이름
     * @param email    사용자 이메일
     * @param password 사용자 비밀번호
     * @return 저장된 사용자의 ID
     */
    Long save(String username, String email, String password);

    /**
     * 특정 사용자 이름이 존재하는지 확인하는 메서드
     *
     * @param username 확인할 사용자 이름
     * @return 사용자 이름이 존재하면 true, 그렇지 않으면 false
     */
    boolean existUsername(String username);

    /**
     * 특정 이메일이 존재하는지 확인하는 메서드
     *
     * @param email 확인할 이메일 주소
     * @return 이메일이 존재하면 true, 그렇지 않으면 false
     */
    boolean existEmail(String email);

    /**
     * 사용자의 비밀번호를 변경하는 메서드
     *
     * @param username    비밀번호를 변경할 사용자 이름
     * @param newPassword 새로운 비밀번호
     */
    void changePassword(String username, String newPassword);

}
