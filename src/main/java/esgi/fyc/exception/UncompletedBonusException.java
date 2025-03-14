package esgi.fyc.exception;

import esgi.fyc.model.money.Money;

public class UncompletedBonusException extends DomainException {
    public UncompletedBonusException(Money bonusWageringLeft) {
        super("Bonus actif non complété : " + bonusWageringLeft + " à miser.");
    }
}
