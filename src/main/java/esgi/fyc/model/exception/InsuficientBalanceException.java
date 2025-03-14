package esgi.fyc.model.exception;

public class InsuficientBalanceException extends DomainException {
   public InsuficientBalanceException() {
      super("Solde insuffisant");
   }
}
