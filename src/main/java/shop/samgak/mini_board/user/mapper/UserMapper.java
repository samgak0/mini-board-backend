package shop.samgak.mini_board.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.entities.User;

/**
 * UserMapper는 User 엔티티와 UserDTO 간의 변환을 담당하는 인터페이스입니다.
 * MapStruct 라이브러리를 사용하여 엔티티와 DTO 간의 매핑을 자동으로 생성합니다.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * User 엔티티를 UserDTO로 변환하는 메서드입니다.
     *
     * @param user User 엔티티 객체
     * @return UserDTO 객체
     */
    UserDTO userToUserDTO(User user);

    /**
     * UserDTO를 User 엔티티로 변환하는 메서드입니다.
     * 비밀번호, 이메일, 생성일 및 수정일 정보는 매핑하지 않습니다.
     *
     * @param userDTO UserDTO 객체
     * @return User 엔티티 객체
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User userDTOToUser(UserDTO userDTO);
}
