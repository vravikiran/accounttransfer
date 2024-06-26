package com.dws.challenge.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dws.challenge.gradle.domain.Account;
import com.dws.challenge.gradle.domain.Transfer;
import com.dws.challenge.gradle.exception.AccountDoesNotExistsException;
import com.dws.challenge.gradle.exception.DuplicateAccountIdException;
import com.dws.challenge.gradle.exception.InsufficientBalanceException;
import com.dws.challenge.gradle.repository.AccountsRepository;
import com.dws.challenge.gradle.service.AccountsService;
import com.dws.challenge.gradle.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class AccountsServiceTest {

	@Mock
	private AccountsRepository accountsRepository;
	@InjectMocks
	private AccountsService accountsService;
	@Mock
	NotificationService notificationService;

	@Test
	void testGetAccount_WhenExists() {
		Account account = getAccount();
		given(accountsRepository.getAccount(any())).willReturn(account);
		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	void testGetAccount_WhenAccountNotExists() {
		given(accountsRepository.getAccount(any())).willThrow(AccountDoesNotExistsException.class);
		assertThrows(AccountDoesNotExistsException.class, () -> accountsRepository.getAccount("1234"));
	}

	@Test
	void testCreateAccount_WithValidData() {
		doNothing().when(accountsRepository).createAccount(any());
		accountsService.createAccount(any());
		verify(accountsRepository, times(1)).createAccount(any());
	}

	@Test
	void testCreateAccount_WithInValidData() {
		doThrow(DuplicateAccountIdException.class).when(accountsRepository).createAccount(any());
		assertThrows(DuplicateAccountIdException.class, () -> accountsService.createAccount(any()));
	}

	@Test
	void testTransfer_WithInvalidToAccount() {
		Transfer transfer = getTransfer();
		given(accountsRepository.getAccount(anyString())).willThrow(AccountDoesNotExistsException.class);
		assertThrows(AccountDoesNotExistsException.class, () -> accountsService.transfetAmount(transfer));
	}

	@Test
	void testTransferWithInsuffAmountInFromAccount() {
		Transfer transfer = new Transfer("Id-123", "Id-124", new BigDecimal(1200));
		given(accountsRepository.getAccount(anyString())).willReturn(getAccount());
		assertThrows(InsufficientBalanceException.class, () -> accountsService.transfetAmount(transfer));
	}

	@Test
	void testTransferWithValidBalAndAccounts() {
		Transfer transfer = getTransfer();
		given(accountsRepository.getAccount(anyString())).willReturn(getAccount());
		accountsService.transfetAmount(transfer);
		verify(accountsRepository, times(2)).updateBalance(any());
	}

	private Account getAccount() {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		return account;
	}

	private Transfer getTransfer() {
		Transfer transfer = new Transfer("Id-123", "Id-124", new BigDecimal(200));
		return transfer;
	}
}
