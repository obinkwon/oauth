package oauth.core.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "user")
public class UserEntity {

	@Id
	@Comment("사용자 ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("사용자 명")
	@Column(nullable = false, length=50)
	private String userName;

	@Comment("이메일")
	@Column(nullable = false, unique = true, length=50)
	private String email;
	
	@Comment("비밀번호")
	@Column(nullable = false, length=100)
	private String password;

	@Comment("생성 시간")
	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false)
	private LocalDateTime created_time;

	@Comment("수정 시간")
	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
	private LocalDateTime updated_time;
}
