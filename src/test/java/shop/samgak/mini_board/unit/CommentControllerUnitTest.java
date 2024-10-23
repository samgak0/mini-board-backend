package shop.samgak.mini_board.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import shop.samgak.mini_board.comment.controllers.CommentController;
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.services.CommentService;
import shop.samgak.mini_board.exceptions.GlobalExceptionHandler;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.security.MyUserDetails;
import shop.samgak.mini_board.user.dto.UserDTO;

public class CommentControllerUnitTest {

        private MockMvc mockMvc;

        @Mock
        private CommentService commentService;

        @InjectMocks
        private CommentController commentController;

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

        @AfterEach
        public void cleanUp() {
                SecurityContextHolder.clearContext();
        }

        @Test
        public void testGetComments() throws Exception {

                setSecurityContext();

                List<CommentDTO> mockComments = new ArrayList<>();
                Long commentId1 = 1L;
                Long commentId2 = 2L;
                UserDTO user = new UserDTO(1L, "user");
                PostDTO post = new PostDTO(1L, user, "Post Title", "Post Content", null, null);
                String content1 = "First Comment";
                String content2 = "Second Comment";
                CommentDTO comment1 = new CommentDTO(commentId1, user, post, null, content1, null, null);
                CommentDTO comment2 = new CommentDTO(commentId2, user, post, null, content2, null, null);

                mockComments.add(comment1);
                mockComments.add(comment2);
                when(commentService.get(1L)).thenReturn(mockComments);

                mockMvc.perform(get("/api/comments/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].content").value("First Comment"))
                                .andExpect(jsonPath("$.data[1].content").value("Second Comment"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void testGetCommentByIdNotFound() throws Exception {
                Long commentId = 1L;

                setSecurityContext();

                when(commentService.get(commentId))
                                .thenThrow(new ResourceNotFoundException("Comment not found with id: " + commentId));

                mockMvc.perform(get("/api/comments/{id}", commentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Comment not found with id: " + commentId))
                                .andExpect(jsonPath("$.code").value("FAILURE"));
        }

        @Test
        public void createCommentUserNotFound() throws Exception {
                String content = "Test Content";

                Authentication authentication = mock(Authentication.class);
                SecurityContext securityContext = mock(SecurityContext.class);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getPrincipal()).thenReturn(null);
                SecurityContextHolder.setContext(securityContext);

                mockMvc.perform(post("/api/comments")
                                .param("content", content))
                                .andExpect(status().isUnauthorized())
                                .andDo(MockMvcResultHandlers.print());
        }

        @Test
        public void createCommentMissingContent() throws Exception {
                setSecurityContext();

                mockMvc.perform(post("/api/comments"))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void createCommentSuccess() throws Exception {
                String content = "Test Content";
                setSecurityContext();

                mockMvc.perform(post("/api/comments")
                                .param("content", content))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isCreated());
        }

        @Test
        public void updateCommentSuccess() throws Exception {
                Long commentId = 1L;
                Long userId = 1L;
                String content = "Updated Content";
                setSecurityContext();
                doNothing().when(commentService).delete(commentId, userId);

                mockMvc.perform(put("/api/comments/{id}", commentId)
                                .param("content", content))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void updateCommentUnauthorized() throws Exception {
                Long commentId = 1L;
                String content = "Updated Content";

                mockMvc.perform(put("/api/comments/{id}", commentId)
                                .param("content", content))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isUnauthorized());
        }

        @Test
        public void deleteCommentSuccess() throws Exception {
                Long commentId = 1L;
                Long userId = 1L;
                setSecurityContext();

                doNothing().when(commentService).delete(commentId, userId);

                mockMvc.perform(delete("/api/comments/{id}", commentId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void deleteCommentUnauthorized() throws Exception {
                Long commentId = 1L;

                mockMvc.perform(delete("/api/comments/{id}", commentId))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isUnauthorized());

        }

        private void setSecurityContext() {
                UserDTO mockUserDTO = new UserDTO(1L, "user");
                MyUserDetails myUserDetails = new MyUserDetails(mockUserDTO, null);

                Authentication authentication = mock(Authentication.class);
                SecurityContext securityContext = mock(SecurityContext.class);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getPrincipal()).thenReturn(myUserDetails);
                SecurityContextHolder.setContext(securityContext);
        }
}
