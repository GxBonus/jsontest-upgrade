package com.example.jsontestupgrade;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String username;
    String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }
}
