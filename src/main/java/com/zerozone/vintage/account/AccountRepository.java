package com.zerozone.vintage.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    Optional<Account> findByEmail(String mail);

    Optional<Account> findByNickname(String nickname);
}
