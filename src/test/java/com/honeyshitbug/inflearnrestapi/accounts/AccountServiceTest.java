package com.honeyshitbug.inflearnrestapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {



  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Test
  void findByUsername() {
    //Given
    String password = "keesun";
    String email = "keesun@email.com";
    Account account = Account.builder()
        .email(email)
        .password(password)
        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
        .build();
    accountRepository.save(account);

    //When
    UserDetailsService userDetailsService = (UserDetailsService) accountService;
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    //Then
    assertThat(userDetails.getPassword()).isEqualTo(password);
  }

  @Test
  void findByUsernameFail() {
    //Given
    String username = "random@email.com";

    //When
    Throwable thrown = catchThrowable(() -> {
      accountService.loadUserByUsername(username);
    });

    //Then
    assertThat(thrown).isInstanceOf(UsernameNotFoundException.class).hasMessageContaining(username);
  }
}