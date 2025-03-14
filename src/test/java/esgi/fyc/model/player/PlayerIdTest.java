package esgi.fyc.model.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testPlayerIdEquals() {
        PlayerId playerId1 = PlayerId.of("1234");
        PlayerId playerId2 = PlayerId.of("1234");
        PlayerId playerId3 = PlayerId.of("5678");

        assertEquals(playerId1, playerId2);
        assertNotEquals(playerId1, playerId3);
    }
}
