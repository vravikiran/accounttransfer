package com.dws.challenge.gradle.repository;

import java.math.BigDecimal;

import com.dws.challenge.gradle.domain.Account;
import com.dws.challenge.gradle.exception.DuplicateAccountIdException;

public interface AccountsRepository {

	void createAccount(Account account) throws DuplicateAccountIdException;

	Account getAccount(String accountId);

	void clearAccounts();

	BigDecimal getBalance(String accountId);

	Account updateBalance(Account account);
}
