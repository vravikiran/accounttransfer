package com.dws.challenge.gradle.service;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dws.challenge.gradle.domain.Account;
import com.dws.challenge.gradle.domain.Transfer;
import com.dws.challenge.gradle.exception.AccountDoesNotExistsException;
import com.dws.challenge.gradle.exception.InsufficientBalanceException;
import com.dws.challenge.gradle.repository.AccountsRepository;

@Service
public class AccountsService {

	@Autowired
	private final AccountsRepository accountsRepository;

	public AccountsRepository getAccountsRepository() {
		return accountsRepository;
	}

	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	@Transactional
	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		Account account = null;
		account = this.accountsRepository.getAccount(accountId);
		if(account == null)
			throw new AccountDoesNotExistsException();
		return account;
	}

	@Transactional
	public void transfetAmount(Transfer transfer) throws AccountDoesNotExistsException, InsufficientBalanceException {
		boolean success = false;
		while (!success) {
			Account fromAccount = getAccount(transfer.getFromAccount());
			Account toAccount = getAccount(transfer.getToAccount());
			Lock sourceLock = fromAccount.getLock();
			Lock toLock = toAccount.getLock(); 
			if (sourceLock.tryLock()) {
				try {
					if (fromAccount.getBalance().subtract(transfer.getAmount()).doubleValue() < 0) {
						throw new InsufficientBalanceException();
					} else {
						if (toLock.tryLock()) {
							try {
								BigDecimal bal = null;
								bal = fromAccount.getBalance().subtract(transfer.getAmount());
								fromAccount.setBalance(bal);
								accountsRepository.updateBalance(fromAccount);
								bal = toAccount.getBalance().add(transfer.getAmount());
								toAccount.setBalance(bal);
								accountsRepository.updateBalance(toAccount);
							} finally {
								toLock.unlock();
							}
							success = true;
						}
					}
					
				} finally {
					sourceLock.unlock();
				}
			}
		}
	}
}
