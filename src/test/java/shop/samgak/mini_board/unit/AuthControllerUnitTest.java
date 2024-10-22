package shop.samgak.mini_board.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import shop.samgak.mini_board.config.GlobalExceptionHandler;
import shop.samgak.mini_board.security.AuthController;
import shop.samgak.mini_board.security.MyUserDetails;
import shop.samgak.mini_board.user.dto.UserDTO;

public class AuthControllerUnitTest {

        private MockMvc mockMvc;

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private UserDetailsService userDetailsService;

        @InjectMocks
        private AuthController authController;

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(authController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
                SecurityContextHolder.clearContext();
        }

        @Test
        public void testLoginSuccess() throws Exception {
                String username = "testUser";
                String password = "testPassword";
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
                UserDetails userDetails = mock(UserDetails.class);

                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);
                when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Login successful"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void testLoginFailure() throws Exception {
                // Arrange
                String username = "invalidUser";
                String password = "invalidPassword";

                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new UsernameNotFoundException("User not found: " + username));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.message")
                                                .value("Invalid credentials : User not found: " + username))
                                .andExpect(jsonPath("$.code").value("FAILURE"));
        }

        @Test
        public void testLogoutSuccess() throws Exception {
                MockHttpSession localSession = new MockHttpSession();
                UserDTO mockUserDTO = new UserDTO();
                mockUserDTO.setId(1L);
                mockUserDTO.setUsername("username");

                SecurityContext securityContext = mock(SecurityContext.class);
                Authentication authentication = mock(Authentication.class);
                MyUserDetails myUserDetails = mock(MyUserDetails.class);

                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getPrincipal()).thenReturn(myUserDetails);
                when(myUserDetails.getUserDTO()).thenReturn(mockUserDTO);

                localSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                                securityContext);

                mockMvc.perform(post("/api/auth/logout")
                                .session(localSession))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Logout successful"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void testLogoutUserNotFound() throws Exception {

                mockMvc.perform(post("/api/auth/logout"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.message").value("User not logged in"))
                                .andExpect(jsonPath("$.code").value("FAILURE"));
        }

}
