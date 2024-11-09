package shop.samgak.mini_board.unit;

import java.util.Map;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.samgak.mini_board.exceptions.UserNotFoundException;
import shop.samgak.mini_board.security.AuthController;
import shop.samgak.mini_board.security.MyUserDetails;
import shop.samgak.mini_board.user.dto.UserDTO;

@ActiveProfiles("test")
@WebMvcTest(controllers = { AuthController.class })
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerUnitTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthenticationManager authenticationManager;

        @MockBean
        private UserDetailsService userDetailsService;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        public void setUp() {
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
                                .content(objectMapper.writeValueAsString(
                                                Map.of("username", username, "password", password))))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("Login successful"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void testLoginFailure() throws Exception {
                String username = "invalidUser";
                String password = "invalidPassword";

                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new UserNotFoundException(username));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                Map.of("username", username, "password", password))))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("User not found: " + username))
                                .andExpect(jsonPath("$.code").value("FAILURE"));
        }

        @Test
        public void testLoginFailureMissingUsername() throws Exception {
                String password = "testPassword";

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("password", password))))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("username: Missing required parameter;"))
                                .andExpect(jsonPath("$.code").value("FAILURE"));
        }

        @Test
        public void testLoginFailureMissingPassword() throws Exception {
                String username = "testUser";

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("username", username))))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("password: Missing required parameter;"))
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
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("Logout successful"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void testLogoutUserNotFound() throws Exception {
                mockMvc.perform(post("/api/auth/logout"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("Authentication is required"))
                                .andExpect(jsonPath("$.code").value("FAILURE"));
        }
}
