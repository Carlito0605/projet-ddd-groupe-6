package esgi.fyc.model.exception;

public class SuspendedPlayerException extends DomainException {
    public SuspendedPlayerException(String reason) {
        super("Le compte du joueur est suspendu : " + reason);
    }
}
