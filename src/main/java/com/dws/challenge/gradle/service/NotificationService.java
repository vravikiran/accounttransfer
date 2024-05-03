package com.dws.challenge.gradle.service;

import org.springframework.stereotype.Service;

import com.dws.challenge.gradle.domain.Account;
@Service
public interface NotificationService {

  void notifyAboutTransfer(Account account, String transferDescription);
}
