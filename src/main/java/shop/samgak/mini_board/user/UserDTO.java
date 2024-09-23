<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/user/dto/UserDTO.java
package shop.samgak.mini_board.user.dto;
========
package shop.samgak.mini_board.user;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/user/UserDTO.java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
}
