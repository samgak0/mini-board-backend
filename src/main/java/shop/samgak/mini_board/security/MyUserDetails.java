package shop.samgak.mini_board.security;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import shop.samgak.mini_board.user.dto.UserDTO;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class MyUserDetails implements UserDetails {
    private UserDTO user;
    private String password;
    private List<GrantedAuthority> authorities;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;

    @JsonCreator
    public MyUserDetails(@JsonProperty("user") UserDTO user,@JsonProperty("password") String password) {
        this.user = user;
        this.password = password;
        this.authorities = new ArrayList<>();
        this.authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public UserDTO getUserDTO() {
        return user;
    }

    @JsonProperty("userDTO")
    public void setUserDTO(UserDTO userDTO) {
        this.user = userDTO;
    }

    public void setUsername(String username) {
        if (this.user == null) {
            this.user = new UserDTO();
        }
        this.user.setUsername(username);
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public void setId(Long id) {
        if (this.user == null) {
            this.user = new UserDTO();
        }
        user.setId(id);
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    public void setAccountNonExpired(boolean isAccountNonExpired) {
        this.isAccountNonExpired = isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean isAccountNonLocked) {
        this.isAccountNonLocked = isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean isCredentialsNonExpired) {
        this.isCredentialsNonExpired = isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
