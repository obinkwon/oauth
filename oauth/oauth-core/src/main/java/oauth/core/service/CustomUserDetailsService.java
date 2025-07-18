package oauth.core.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import oauth.core.entity.UserEntity;
import oauth.core.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("사용자 없음: " + id));

//        return new org.springframework.security.core.userdetails.User(
//                userEntity.getEmail(),
//                userEntity.getPassword(),
//                List.of(new SimpleGrantedAuthority("ROLE_USER"))
//        );
        return null;
    }
}

