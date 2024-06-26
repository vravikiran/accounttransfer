package com.dws.challenge.gradle.domain;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Transfer {
	@NotNull
	@NotEmpty
	private final String fromAccount;
	@NotNull
	@NotEmpty
	private final String toAccount;
	@NotEmpty
	private final BigDecimal amount;
	
	@JsonCreator
	public Transfer(@JsonProperty("fromAccount") String fromAccount, @JsonProperty("toAccount") String toAccount, @JsonProperty("amount") BigDecimal amount) {
		super();
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
	}

	public String getFromAccount() {
		return fromAccount;
	}

	public String getToAccount() {
		return toAccount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(amount, fromAccount, toAccount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transfer other = (Transfer) obj;
		return Objects.equals(amount, other.amount) && Objects.equals(fromAccount, other.fromAccount)
				&& Objects.equals(toAccount, other.toAccount);
	}
}
