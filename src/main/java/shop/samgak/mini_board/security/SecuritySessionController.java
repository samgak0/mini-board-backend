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

/**
 * 현재 활성화된 세션 정보를 제공하는 REST API를 정의
 */
@RestController
public class SecuritySessionController {

    // Spring Security의 SessionRegistry를 사용하여 활성 사용자 세션을 관리
    @Autowired
    private SessionRegistry sessionRegistry;
    // RedisTemplate을 사용하여 Redis에 저장된 세션 데이터를 조회
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 현재 활성화된 세션의 사용자 목록을 반환하는 API 엔드포인트
     * 
     * @return 현재 활성화된 사용자 세션 목록
     */
    @GetMapping("/sessions")
    public List<Object> getActiveSessions() {
        return sessionRegistry.getAllPrincipals();
    }

    /**
     * Redis에 저장된 모든 세션 데이터를 반환하는 API 엔드포인트
     * 
     * @return Redis에 저장된 모든 세션 데이터
     */
    @GetMapping("/sessions-redis")
    public Map<String, Object> getActiveSessionsRedis() {
        Map<String, Object> allData = new HashMap<>();
        Set<String> keys = redisTemplate.keys("*");

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                String type = redisTemplate.type(key).code();
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
