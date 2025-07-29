package oauth.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class UserEntity implements UserDetails {

	@Id
	@Comment("사용자 ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("사용자 명")
	@Column(nullable = false, length=50)
	private String username;

	@Comment("이메일")
	@Column(nullable = false, unique = true, length=50)
	private String email;
	
	@Comment("비밀번호")
	@Column(length=100)
	private String password;

	@Comment("생성 시간")
	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false)
	private LocalDateTime created_time;

	@Comment("수정 시간")
	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
	private LocalDateTime updated_time;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("USER"));
	}

}
