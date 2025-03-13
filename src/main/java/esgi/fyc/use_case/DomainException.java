package esgi.fyc.use_case;

public class DomainException extends RuntimeException {
   public DomainException(String message) {
      super(message);
   }
}