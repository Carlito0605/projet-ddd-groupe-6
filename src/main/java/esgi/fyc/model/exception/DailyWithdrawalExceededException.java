package esgi.fyc.model.exception;

import esgi.fyc.model.money.Money;

public class DailyWithdrawalExceededException extends DomainException {
   public DailyWithdrawalExceededException(Money dailyLimit) {
      super("Limite journalière dépassée : " + dailyLimit);
   }
}
