package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    Account findByEmail(String mail);
}
