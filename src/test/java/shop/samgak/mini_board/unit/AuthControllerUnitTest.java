package shop.samgak.mini_board.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import shop.samgak.mini_board.exceptions.UserNotFoundException;
import shop.samgak.mini_board.security.AuthController;
import shop.samgak.mini_board.security.MyUserDetails;
import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * AuthController의 단위 테스트를 위한 클래스입니다.
 */
@ActiveProfiles("test")
@WebMvcTest(controllers = { AuthController.class })
@AutoConfigureMockMvc(addFilters = false) // 필터를 비활성화하여 간단히 테스트할 수 있도록 설정합니다.
public class AuthControllerUnitTest {

        @Autowired
        private MockMvc mockMvc; // MockMvc를 사용하여 컨트롤러에 요청을 보냅니다.

        @MockBean
        private AuthenticationManager authenticationManager; // 인증 매니저를 모킹하여 인증 로직을 테스트합니다.

        @MockBean
        private UserDetailsService userDetailsService; // UserDetailsService를 모킹하여 사용자의 인증 정보를 테스트합니다.

        @BeforeEach
        public void setUp() {
                // 각 테스트 케이스가 실행되기 전의 준비 작업을 여기서 수행합니다. (현재는 별도 작업 없음)
        }

        // 로그인 성공 시나리오 테스트
        @Test
        public void testLoginSuccess() throws Exception {
                String username = "testUser";
                String password = "testPassword";
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
                UserDetails userDetails = mock(UserDetails.class); // UserDetails를 모킹하여 반환값을 설정합니다.

                // AuthenticationManager가 정상적으로 인증을 반환하도록 설정합니다.
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);

                // UserDetailsService가 요청된 사용자 이름에 대해 올바른 UserDetails를 반환하도록 설정합니다.
                when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

                // 로그인 요청을 POST로 보낸 후 결과를 검증합니다.
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
                                .andExpect(status().isOk()) // 응답 상태가 200 OK인지 확인합니다.
                                .andExpect(jsonPath("$.message").value("Login successful")) // 응답 메시지를 확인합니다.
                                .andExpect(jsonPath("$.code").value("SUCCESS")); // 응답 코드가 SUCCESS인지 확인합니다.
        }

        // 로그인 실패 시나리오 테스트 (사용자를 찾을 수 없는 경우)
        @Test
        public void testLoginFailure() throws Exception {
                String username = "invalidUser";
                String password = "invalidPassword";

                // 인증 실패 시 UserNotFoundException이 발생하도록 설정합니다.
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new UserNotFoundException(username));

                // 로그인 요청을 보내고 실패 여부를 검증합니다.
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
                                .andExpect(status().isUnauthorized()) // 응답 상태가 401 Unauthorized인지 확인합니다.
                                .andExpect(jsonPath("$.message").value("User not found: " + username)) // 응답 메시지를 확인합니다.
                                .andExpect(jsonPath("$.code").value("FAILURE")); // 응답 코드가 FAILURE인지 확인합니다.
        }

        // 로그아웃 성공 시나리오 테스트
        @Test
        public void testLogoutSuccess() throws Exception {
                MockHttpSession localSession = new MockHttpSession(); // Mock 세션을 생성합니다.
                UserDTO mockUserDTO = new UserDTO();
                mockUserDTO.setId(1L);
                mockUserDTO.setUsername("username");

                SecurityContext securityContext = mock(SecurityContext.class); // SecurityContext를 모킹합니다.
                Authentication authentication = mock(Authentication.class); // Authentication 객체를 모킹합니다.
                MyUserDetails myUserDetails = mock(MyUserDetails.class); // 사용자 세부 정보를 모킹합니다.

                // SecurityContext에 인증 정보를 설정합니다.
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getPrincipal()).thenReturn(myUserDetails);
                when(myUserDetails.getUserDTO()).thenReturn(mockUserDTO);

                // 세션에 SecurityContext를 설정합니다.
                localSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                                securityContext);

                // 로그아웃 요청을 보낸 후 결과를 검증합니다.
                mockMvc.perform(post("/api/auth/logout")
                                .session(localSession))
                                .andExpect(status().isOk()) // 응답 상태가 200 OK인지 확인합니다.
                                .andExpect(jsonPath("$.message").value("Logout successful")) // 응답 메시지를 확인합니다.
                                .andExpect(jsonPath("$.code").value("SUCCESS")); // 응답 코드가 SUCCESS인지 확인합니다.
        }

        // 로그아웃 실패 시나리오 테스트 (사용자가 로그인하지 않은 경우)
        @Test
        public void testLogoutUserNotFound() throws Exception {
                // 로그인하지 않은 상태에서 로그아웃 요청을 보냅니다.
                mockMvc.perform(post("/api/auth/logout"))
                                .andExpect(status().isUnauthorized()) // 응답 상태가 401 Unauthorized인지 확인합니다.
                                .andExpect(jsonPath("$.message").value("Authentication is required")) // 응답 메시지를 확인합니다.
                                .andExpect(jsonPath("$.code").value("FAILURE")); // 응답 코드가 FAILURE인지 확인합니다.
        }
}
