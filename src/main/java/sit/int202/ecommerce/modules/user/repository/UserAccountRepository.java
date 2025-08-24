package sit.int202.ecommerce.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int202.ecommerce.modules.user.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
