package esgi.fyc.exception;

import esgi.fyc.model.money.Money;

public class UnverifiedKycException extends DomainException {
    public UnverifiedKycException(Money kycThreshold) {
        super("Retrait supérieur à " + kycThreshold + ", vérification KYC requise.");
    }
}
