package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    Optional<Account> findByEmail(String mail);

    Optional<Account> findByNickname(String nickname);
}
