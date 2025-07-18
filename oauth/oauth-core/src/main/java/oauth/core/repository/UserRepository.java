package oauth.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import oauth.core.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
