package com.zerozone.vintage.account;

import com.zerozone.vintage.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Primary
@Transactional
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByEmail(emailOrNickname);

        Account account = accountOptional.orElseGet(() -> accountRepository.findByNickname(emailOrNickname)
                .orElseThrow(() -> new UsernameNotFoundException(emailOrNickname)));

        return new UserAccount(account);
    }
}