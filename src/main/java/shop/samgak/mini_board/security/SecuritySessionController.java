package shop.samgak.mini_board.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuritySessionController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/sessions")
    public List<Object> getActiveSessions() {
        return sessionRegistry.getAllPrincipals();
    }
}
