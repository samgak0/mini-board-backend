package shop.samgak.mini_board.user.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보를 저장하는 엔티티 클래스
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * 사용자 ID (기본 키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USERS_SEQ", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 사용자 이름 (고유)
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * 사용자 이메일 (고유)
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * 사용자 비밀번호 (해시 형태로 저장)
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 사용자 계정 생성 일시
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    /**
     * 사용자 계정 정보 마지막 수정 일시
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /**
     * 사용자 삭제 일시 (논리적 삭제 처리)
     */
    @Column(name = "deleted_at")
    private Instant deletedAt = null;
}
