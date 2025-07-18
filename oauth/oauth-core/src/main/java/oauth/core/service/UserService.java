package oauth.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth.core.entity.UserEntity;
import oauth.core.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// 유저 정보 가져오기 (Id)
	public Optional<UserEntity> getUserData(Long id) {
		return userRepository.findById(id);
	}
	
	// 유저 정보 등록
	public UserEntity createUserData(String username, String email, String password) {
		UserEntity userEntity = UserEntity.builder()
				.userName(username)
				.email(email)
				.password(passwordEncoder.encode(password))
				.build();
		userRepository.save(userEntity);

		return userEntity;
	}

}
