package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;


@Service
public class AccountService {
    AccountRepository dao;
    @Autowired
    public AccountService(AccountRepository dao) {
        this.dao = dao;
    }

    public Optional<Account> create(Account newUser) {
        return Optional.of(dao.save(newUser));
    }

    public boolean usernameExists(String username) {
        Account a = dao.findAccountByUsername(username);
        return a != null;
    }

    public Optional<Account> getValid(Account user) {
        return Optional.ofNullable(dao.findAccountByUsernameAndPassword(user.getUsername(), user.getPassword()));
    }

    public Optional<Account> getById(int accountId) {
        return Optional.ofNullable(dao.getById(accountId));
    }

}
