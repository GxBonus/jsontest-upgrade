package com.example.jsontestupgrade;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAccountReposittory extends CrudRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsernameAndPassword(String username, String password);

    Optional<UserAccount> findByUsername(String username);
}
