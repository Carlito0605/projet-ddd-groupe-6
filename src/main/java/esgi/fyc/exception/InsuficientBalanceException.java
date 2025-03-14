package esgi.fyc.exception;

public class InsuficientBalanceException extends DomainException {
   public InsuficientBalanceException() {
      super("Solde insuffisant");
   }
}
