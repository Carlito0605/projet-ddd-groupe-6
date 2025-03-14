package esgi.fyc.model.player;

import static org.junit.jupiter.api.Assertions.*;

import esgi.fyc.model.money.Currency;
import esgi.fyc.model.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {

   private Player player;

   @BeforeEach
   void setUp() {
      player = new Player(new PlayerId("71234"), new Money(500, Currency.EUR));
   }

   @Test
   void testInitialBalance() {
      assertEquals(new Money(500, Currency.EUR), player.getBalance());
   }

   @Test
   void testDeposit() {
      player.addToBalance(new Money(200, Currency.EUR));
      assertEquals(new Money(700, Currency.EUR), player.getBalance());
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
      Money bonus = new Money(100, Currency.EUR);
      Money wagering = new Money(50, Currency.EUR);
      player.addBonus(bonus, wagering);

      assertEquals(bonus, player.getBonusBalance());
      assertEquals(wagering, player.getBonusWageringLeft());
   }

   @Test
   void testReduceWageringRequirement() {
      player.addBonus(new Money(100, Currency.EUR), new Money(50, Currency.EUR));
      player.reduceWageringRequirement(new Money(30, Currency.EUR));
      assertEquals(new Money(20, Currency.EUR), player.getBonusWageringLeft());
      player.reduceWageringRequirement(new Money(25, Currency.EUR));
      assertEquals(Money.ZERO, player.getBonusWageringLeft());
   }
}