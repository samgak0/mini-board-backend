package shop.samgak.mini_board.user.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class UserDTO {
    private Long id;
    private String username;
}
