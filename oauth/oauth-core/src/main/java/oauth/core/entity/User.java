package oauth.core.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "user")
public class User {
	@Id
	@Comment("이메일")
	@Column(nullable = false, unique = true, length=50)
	private String email;
	
	@Comment("비밀번호")
	@Column(nullable = false, unique = true, length=50)
	private String password;

	@Comment("생성 시간")
	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false)
	private LocalDateTime created_time;

	@Comment("수정 시간")
	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
	private LocalDateTime updated_time;
}
