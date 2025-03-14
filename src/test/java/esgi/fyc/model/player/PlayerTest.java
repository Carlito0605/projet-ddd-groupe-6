package esgi.fyc.model.player;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {

   private Player player;

   @BeforeEach
   void setUp() {
      player = new Player(new PlayerId("71234"), BigDecimal.valueOf(500));
   }

   @Test
   void testInitialBalance() {
      assertEquals(BigDecimal.valueOf(500), player.getBalance());
   }

   @Test
   void testDeposit() {
      player.addToBalance(BigDecimal.valueOf(200));
      assertEquals(BigDecimal.valueOf(700), player.getBalance());
   }

   @Test
   void testSuspend() {
      player.suspend("Fraude détectée");

      assertTrue(player.isSuspended());
      assertEquals("Fraude détectée", player.getSuspensionReason());
   }

   @Test
   void testVerifyKyc() {
      assertFalse(player.isKycVerified());

      player.verifyKyc();
      assertTrue(player.isKycVerified());
   }

   @Test
   void testAddBonus() {
      player.addBonus(BigDecimal.valueOf(100), BigDecimal.valueOf(200));

      assertEquals(BigDecimal.valueOf(100), player.getBonusBalance());
      assertEquals(BigDecimal.valueOf(200), player.getBonusWageringLeft());
   }

   @Test
   void testReduceWageringRequirement() {
      player.addBonus(BigDecimal.valueOf(100), BigDecimal.valueOf(50));
      player.reduceWageringRequirement(BigDecimal.valueOf(30));
      assertEquals(BigDecimal.valueOf(20), player.getBonusWageringLeft());
      player.reduceWageringRequirement(BigDecimal.valueOf(25));
      assertEquals(BigDecimal.ZERO, player.getBonusWageringLeft());
   }
}