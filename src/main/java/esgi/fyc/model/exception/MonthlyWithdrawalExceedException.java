package esgi.fyc.model.exception;

import esgi.fyc.model.money.Money;

public class MonthlyWithdrawalExceedException extends DomainException {
    public MonthlyWithdrawalExceedException(Money monthlyLimit) {
        super("Limite mensuelle dépassée : " + monthlyLimit);
    }
}
