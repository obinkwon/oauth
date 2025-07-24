package oauth.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth.core.entity.UserEntity;
import oauth.core.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// 유저 정보 등록
	public UserEntity createUserData(String username, String email, String password) {
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(username);
		userEntity.setEmail(email);
		userEntity.setPassword(passwordEncoder.encode(password));
		userRepository.save(userEntity);

		return userEntity;
	}
}
