package shop.samgak.mini_board.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * MyUserDetails 클래스는 Spring Security의 UserDetails 인터페이스를 구현하여 사용자 정보를 관리하는 역할을
 * 함
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class MyUserDetails implements UserDetails {
    // 사용자 정보를 저장하는 UserDTO 객체
    private UserDTO userDTO;
    // 사용자 비밀번호
    private String password;
    // 사용자 권한 목록
    private List<GrantedAuthority> authorities;
    // 계정 만료 여부 (기본값: 만료되지 않음)
    private boolean isAccountNonExpired = true;
    // 계정 잠금 여부 (기본값: 잠기지 않음)
    private boolean isAccountNonLocked = true;
    // 자격 증명 만료 여부 (기본값: 만료되지 않음)
    private boolean isCredentialsNonExpired = true;
    // 계정 활성화 여부 (기본값: 활성화됨)
    private boolean isEnabled = true;

    /**
     * JSON으로부터 객체를 생성하기 위한 생성자
     * 
     * @param user     UserDTO 객체
     * @param password 사용자 비밀번호
     */
    @JsonCreator
    public MyUserDetails(@JsonProperty("user") UserDTO user, @JsonProperty("password") String password) {
        this.userDTO = user;
        this.password = password;
        this.authorities = new ArrayList<>();
        // 기본 권한으로 ROLE_USER 추가
        this.authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * UserDTO 객체 반환
     * 
     * @return UserDTO 사용자 정보 객체
     */
    public UserDTO getUserDTO() {
        return userDTO;
    }

    /**
     * UserDTO 객체 설정
     * 
     * @param user 사용자 정보 객체
     */
    @JsonProperty("user")
    public void setUserDTO(UserDTO user) {
        this.userDTO = user;
    }

    /**
     * 사용자 이름 설정
     * 
     * @param username 사용자 이름
     */
    public void setUsername(String username) {
        if (this.userDTO == null) {
            this.userDTO = new UserDTO();
        }
        this.userDTO.setUsername(username);
    }

    /**
     * 사용자 이름 반환
     * 
     * @return 사용자 이름
     */
    @Override
    public String getUsername() {
        return userDTO.getUsername();
    }

    /**
     * 사용자 ID 설정
     * 
     * @param id 사용자 ID
     */
    public void setId(Long id) {
        if (this.userDTO == null) {
            this.userDTO = new UserDTO();
        }
        userDTO.setId(id);
    }

    /**
     * 사용자 비밀번호 반환
     * 
     * @return 사용자 비밀번호
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * 사용자 비밀번호 설정
     * 
     * @param password 사용자 비밀번호
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 사용자 권한 목록 반환
     * 
     * @return 사용자 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 사용자 권한 목록 설정
     * 
     * @param authorities 사용자 권한 목록
     */
    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * 계정 만료 여부 반환
     * 
     * @return 계정이 만료되지 않았으면 true 반환
     */
    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    /**
     * 계정 만료 여부 설정
     * 
     * @param isAccountNonExpired 계정 만료 여부
     */
    public void setAccountNonExpired(boolean isAccountNonExpired) {
        this.isAccountNonExpired = isAccountNonExpired;
    }

    /**
     * 계정 잠금 여부 반환
     * 
     * @return 계정이 잠기지 않았으면 true 반환
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    /**
     * 계정 잠금 여부 설정
     * 
     * @param isAccountNonLocked 계정 잠금 여부
     */
    public void setAccountNonLocked(boolean isAccountNonLocked) {
        this.isAccountNonLocked = isAccountNonLocked;
    }

    /**
     * 자격 증명 만료 여부 반환
     * 
     * @return 자격 증명이 만료되지 않았으면 true 반환
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    /**
     * 자격 증명 만료 여부 설정
     * 
     * @param isCredentialsNonExpired 자격 증명 만료 여부
     */
    public void setCredentialsNonExpired(boolean isCredentialsNonExpired) {
        this.isCredentialsNonExpired = isCredentialsNonExpired;
    }

    /**
     * 계정 활성화 여부 반환
     * 
     * @return 계정이 활성화되었으면 true 반환
     */
    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * 계정 활성화 여부 설정
     * 
     * @param isEnabled 계정 활성화 여부
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
