package shop.samgak.mini_board.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuritySessionController {

    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/sessions")
    public List<Object> getActiveSessions() {
        return sessionRegistry.getAllPrincipals();
    }

    @GetMapping("/sessions-redis")
    public Map<String, Object> getActiveSessionsRedis() {
        Map<String, Object> allData = new HashMap<>();
        Set<String> keys = redisTemplate.keys("*");

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                String type = redisTemplate.type(key).code(); // 키의 데이터 타입 확인
                switch (type) {
                    case "string" -> {
                        Object value = redisTemplate.opsForValue().get(key);
                        if (value != null) {
                            allData.put(key, value);
                        }
                    }
                    case "list" -> {
                        Object listValues = redisTemplate.opsForList().range(key, 0, -1);
                        allData.put(key, listValues);
                    }
                    case "hash" -> {
                        Object hashValues = redisTemplate.opsForHash().entries(key);
                        allData.put(key, hashValues);
                    }
                    default -> allData.put(key, "Unsupported data type: " + type);
                }
            }
        }
        return allData;
    }
}
