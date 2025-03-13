package esgi.fyc.model.playerId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerIdTest {
    @Test
    void testPlayerIdThrowsExceptionWhenValueIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> PlayerId.of(null)
        );
        assertTrue(ex.getMessage().contains("L'identifiant joueur ne peut pas être vide."));
    }

    @Test
    void testPlayerIdThrowsExceptionWhenValueIsEmpty() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> PlayerId.of("")
        );
        assertTrue(ex.getMessage().contains("L'identifiant joueur ne peut pas être vide."));
    }
}
