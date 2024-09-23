<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/entities/Post.java
package shop.samgak.mini_board.post.entities;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/Post.java

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/entities/Post.java
import shop.samgak.mini_board.user.entities.User;
========
import shop.samgak.mini_board.user.User;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/Post.java

@Entity
@Table(name = "posts")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;
    @Column(name = "createdAt")
    private LocalDateTime created_at;
    @Column(name = "updatedAt")
    private LocalDateTime updated_at;
}
