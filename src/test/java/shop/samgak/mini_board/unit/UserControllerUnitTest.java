package shop.samgak.mini_board.unit;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.samgak.mini_board.security.WithMockMyUserDetails;
import shop.samgak.mini_board.user.controllers.UserController;
import shop.samgak.mini_board.user.services.UserService;
import shop.samgak.mini_board.utility.ApiResponse;

@ActiveProfiles("test")
@WebMvcTest(controllers = { UserController.class })
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerUnitTest {

        // Constants for API paths, parameters, and JSON fields
        private static final String API_USERS_CHECK_USERNAME = "/api/users/check/username";
        private static final String API_USERS_CHECK_EMAIL = "/api/users/check/email";
        private static final String API_USERS_REGISTER = "/api/users/register";
        private static final String API_USERS_PASSWORD = "/api/users/password";
        private static final String API_USERS_ME = "/api/users/me";

        private static final String JSON_PATH_MESSAGE = "$.message";
        private static final String JSON_PATH_CODE = "$.code";
        private static final String JSON_PATH_DATA = "$.data";

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        public void setUp() {
                // 각 테스트 실행 전에 필요한 설정 작업 수행
        }

        @AfterEach
        public void cleanUp() {
                // 각 테스트 실행 후 정리 작업 수행
        }

        @Test
        public void testCheckUsernameSuccess() throws Exception {
                // 사용 가능한 사용자명을 확인하는 테스트
                String username = "testUser";

                // UserService의 existUsername 메서드가 false를 반환하도록 설정
                when(userService.existUsername(username)).thenReturn(false);

                // 요청 본문 생성 - UsernameRequest 객체를 JSON 문자열로 변환
                String requestBody = objectMapper.writeValueAsString(new UsernameRequest(username));

                // API 호출 및 결과 검증
                MvcResult result = mockMvc.perform(post(API_USERS_CHECK_USERNAME)
                                .contentType(MediaType.APPLICATION_JSON) // 요청 콘텐츠 타입 설정
                                .content(requestBody)) // 요청 본문 추가
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK 기대
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Username is available")) // 응답 메시지 검증
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value(ApiResponse.Code.SUCCESS.toString())) // 응답 코드 검증
                                .andReturn();

                // 세션 검사 - 사용자명이 세션에 저장되었는지 확인
                MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
                if (session == null) {
                        fail("session is null"); // 세션이 없는 경우 테스트 실패
                } else {
                        assertThat(session.getAttribute(UserController.SESSION_CHECKED_USER))
                                        .isEqualTo(username); // 세션에 사용자명이 저장되었는지 확인
                }
        }

        @Test
        public void testCheckEmailSuccess() throws Exception {
                // 사용 가능한 이메일을 확인하는 테스트
                String email = "test@example.com";

                // UserService의 existEmail 메서드가 false를 반환하도록 설정
                when(userService.existEmail(email)).thenReturn(false);

                // 요청 본문 생성 - EmailRequest 객체를 JSON 문자열로 변환
                String requestBody = objectMapper.writeValueAsString(new EmailRequest(email));

                // API 호출 및 결과 검증
                MvcResult result = mockMvc.perform(post(API_USERS_CHECK_EMAIL)
                                .contentType(MediaType.APPLICATION_JSON) // 요청 콘텐츠 타입 설정
                                .content(requestBody)) // 요청 본문 추가
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK 기대
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Email is available")) // 응답 메시지 검증
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value(ApiResponse.Code.SUCCESS.toString())) // 응답 코드 검증
                                .andReturn();

                // 세션 검사 - 이메일이 세션에 저장되었는지 확인
                MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
                if (session == null) {
                        fail("session is null"); // 세션이 없는 경우 테스트 실패
                } else {
                        assertThat(
                                        session.getAttribute(UserController.SESSION_CHECKED_EMAIL))
                                        .isEqualTo(email); // 세션에 이메일이 저장되었는지 확인
                }
        }

        @Test
        public void testRegisterSuccess() throws Exception {
                // 새로운 사용자 등록 성공 테스트
                String username = "newUser";
                String email = "newuser@example.com";
                String password = "password123Q!";
                Long userId = 1L;

                // UserService의 save 메서드가 userId를 반환하도록 설정
                when(userService.save(username, email, password)).thenReturn(userId);

                // 요청 본문 생성 - RegisterRequest 객체를 JSON 문자열로 변환
                String requestBody = objectMapper.writeValueAsString(new RegisterRequest(username, email, password));

                // 세션에 사용자명과 이메일 저장 - 등록 전에 사용자명과 이메일이 확인된 상태를 가정
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(UserController.SESSION_CHECKED_USER, username);
                session.setAttribute(UserController.SESSION_CHECKED_EMAIL, email);

                // API 호출 및 결과 검증
                mockMvc.perform(post(API_USERS_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON) // 요청 콘텐츠 타입 설정
                                .session(session) // 기존 세션 사용
                                .content(requestBody)) // 요청 본문 추가
                                .andExpect(status().isCreated()) // HTTP 상태 코드 201 Created 기대
                                .andExpect(header().string("Location", "/api/users/1/info")) // Location 헤더의 값 검증
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Registration successful")) // 성공 메시지 검증
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value(ApiResponse.Code.SUCCESS.toString())); // 응답 코드 검증
        }

        @Test
        @WithMockMyUserDetails
        public void testChangePasswordSuccess() throws Exception {
                // 비밀번호 변경 성공 테스트
                String validPassword = "ValidPassword1!";

                // 요청 본문 생성 - PasswordRequest 객체를 JSON 문자열로 변환
                String requestBody = objectMapper.writeValueAsString(new PasswordRequest(validPassword));

                // API 호출 및 결과 검증
                mockMvc.perform(put(API_USERS_PASSWORD)
                                .contentType(MediaType.APPLICATION_JSON) // 요청 콘텐츠 타입 설정
                                .content(requestBody)) // 요청 본문 추가
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK 기대
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Password change successful")) // 성공 메시지
                                                                                      // 검증
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value(ApiResponse.Code.SUCCESS.toString())); // 응답 코드 검증
        }

        @Test
        @WithMockMyUserDetails
        public void testChangePasswordInvalidFailure() throws Exception {
                // 잘못된 비밀번호 형식으로 변경 시도 시 오류 발생 테스트
                String invalidPassword = "short"; // 비밀번호가 너무 짧음

                // 요청 본문 생성 - PasswordRequest 객체를 JSON 문자열로 변환
                String requestBody = objectMapper.writeValueAsString(new PasswordRequest(invalidPassword));

                // API 호출 및 결과 검증 - HTTP 상태 코드 400 Bad Request 기대
                mockMvc.perform(put(API_USERS_PASSWORD)
                                .contentType(MediaType.APPLICATION_JSON) // 요청 콘텐츠 타입 설정
                                .content(requestBody)) // 요청 본문 추가
                                .andExpect(status().isBadRequest()) // HTTP 상태 코드 400 기대
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("password: Invalid password format;")) // 오류 메시지 검증
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value(ApiResponse.Code.FAILURE.toString())); // 응답 코드 검증
        }

        @Test
        @WithMockMyUserDetails
        public void testMeSuccess() throws Exception {
                // 로그인된 사용자가 자신의 정보를 성공적으로 조회하는 테스트
                mockMvc.perform(get(API_USERS_ME)
                                .contentType(MediaType.APPLICATION_JSON)) // 요청 콘텐츠 타입 설정
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK 기대
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Login status")) // 성공 메시지 검증
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value(ApiResponse.Code.SUCCESS.toString())) // 응답 코드 검증
                                .andExpect(jsonPath(JSON_PATH_DATA).exists()); // 데이터 필드가 존재하는지 검증
        }

        @Test
        public void testMeUnauthorized() throws Exception {
                // 인증되지 않은 사용자가 자신의 정보를 요청할 때의 테스트
                mockMvc.perform(get(API_USERS_ME)
                                .contentType(MediaType.APPLICATION_JSON)) // JSON 타입으로 요청
                                .andExpect(status().isUnauthorized()) // 인증되지 않았으므로 401 Unauthorized 기대
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Authentication is required")) // 응답 메시지가 인증 필요임을 확인
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value(ApiResponse.Code.FAILURE.toString())); // 실패 코드 확인
        }

        record UsernameRequest(String username) {
        }

        record EmailRequest(String email) {
        }

        record RegisterRequest(String username, String email, String password) {
        }

        record PasswordRequest(String password) {
        }
}