package shop.samgak.mini_board.user.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.services.UserService;
import shop.samgak.mini_board.utility.ApiResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/")
public class UserController {

    final UserService userService;

    @PostMapping("register")
    public ResponseEntity<ApiResponse> register(@RequestParam String username, @RequestParam String email,
            @RequestParam String password) {
        try {
            Long id = userService.save(username, email, password);
            URI location = URI.create("/api/users/" + id + "/info");
            return ResponseEntity.created(location)
                    .body(new ApiResponse("Register successful", true));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PutMapping("{id}/password")
    public ResponseEntity<ApiResponse> changePassword(@PathVariable String id) {
        return ResponseEntity
                .ok(new ApiResponse("change successful " + id, true));
    }
}