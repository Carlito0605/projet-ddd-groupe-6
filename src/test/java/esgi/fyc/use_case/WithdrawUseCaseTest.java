package esgi.fyc.use_case;

import esgi.fyc.model.exception.*;
import esgi.fyc.model.money.Currency;
import esgi.fyc.model.money.Money;
import esgi.fyc.model.player.Player;
import esgi.fyc.model.player.PlayerRepository;
import esgi.fyc.model.player.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WithdrawUseCaseTest {

   @Mock
   PlayerRepository playerRepository;

   @InjectMocks
   WithdrawUseCase withdrawUseCase;

   @BeforeEach
   void setUp() {
      MockitoAnnotations.openMocks(this);
   }

   @Test
   void testWithdrawValid() {
      String playerId = "1234";

      Money initialBalance = new Money(2000, Currency.EUR);
      Player player = new Player(PlayerId.of(playerId), initialBalance);
      player.verifyKyc();
      when(playerRepository.find(playerId)).thenReturn(player);

      Money amount = new Money(1000, Currency.EUR);
      withdrawUseCase.execute(playerId, amount.getAmount());

      assertEquals(new Money(1000, Currency.EUR), player.getBalance(),
                   "Le solde devrait être 2000 - 1000 = 1000");
      assertEquals(new Money(1000, Currency.EUR), player.getDailyWithdrawal(LocalDate.now()));
      verify(playerRepository).save(player);
   }

   @Test
   void testWithdrawSuspendedAccount() {
      String playerId = "2345";
      Player player = new Player(PlayerId.of(playerId), new Money(500, Currency.EUR));
      player.suspend("Fraude détectée");
      when(playerRepository.find(playerId)).thenReturn(player);

      assertThrows(
            SuspendedPlayerException.class,
            () -> withdrawUseCase.execute(playerId, 100)
      );
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawInsufficientBalance() {
      String playerId = "3456";
      Player player = new Player(PlayerId.of(playerId), new Money(100, Currency.EUR));
      when(playerRepository.find(playerId)).thenReturn(player);

      assertThrows(
            InsuficientBalanceException.class,
            () -> withdrawUseCase.execute(playerId, 200)
      );
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawDailyLimitExceeded() {
      String playerId = "4567";
      Player player = new Player(PlayerId.of(playerId), new Money(3000, Currency.EUR));
      player.verifyKyc();
      player.recordWithdrawal(new Money(900, Currency.EUR), LocalDate.now());
      when(playerRepository.find(playerId)).thenReturn(player);

      assertThrows(
            DailyWithdrawalExceededException.class,
            () -> withdrawUseCase.execute(playerId, 200)
      );
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawMonthlyLimitExceeded() {
      String playerId = "5678";
      Player player = new Player(PlayerId.of(playerId), new Money(6000, Currency.EUR));
      player.verifyKyc();

      LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
      Money monthlyWidrawal = new Money(1000, Currency.EUR);
      for (int i = 0; i < 5; i++) {
         player.recordWithdrawal(monthlyWidrawal, firstDayOfMonth.plusDays(i));
      }

      when(playerRepository.find(playerId)).thenReturn(player);

      assertThrows(
            MonthlyWithdrawalExceedException.class,
            () -> withdrawUseCase.execute(playerId, 100) // total : 5100€
      );
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawOverKycThresholdWithoutKyc() {
      String playerId = "6789";
      Player player = new Player(PlayerId.of(playerId), new Money(3000, Currency.EUR));
      when(playerRepository.find(playerId)).thenReturn(player);

      assertThrows(
            UnverifiedKycException.class,
            () -> withdrawUseCase.execute(playerId, 2500)
      );
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawUnderKycThresholdWithoutKyc() {
      String playerId = "7890";
      Player player = new Player(PlayerId.of(playerId), new Money(2500, Currency.EUR));
      when(playerRepository.find(playerId)).thenReturn(player);

      withdrawUseCase.execute(playerId, 500);

      assertEquals(new Money(2000, Currency.EUR), player.getBalance(),
              "Le solde devrait être 2500 - 500 = 2000");
      assertEquals(new Money(500, Currency.EUR), player.getDailyWithdrawal(LocalDate.now()));
      verify(playerRepository).save(player);
   }

   @Test
   void testWithdrawWithActiveBonus() {
      String playerId = "8901";
      Player player = new Player(PlayerId.of(playerId), new Money(500, Currency.EUR));
      player.addBonus(new Money(100, Currency.EUR), new Money(50, Currency.EUR));
      when(playerRepository.find(playerId)).thenReturn(player);

      assertThrows(
            UncompletedBonusException.class,
            () -> withdrawUseCase.execute(playerId, 100)
      );
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawWithBonusWageringLeft() {
      String playerId = "8901";
      Player player = new Player(PlayerId.of(playerId), new Money(100, Currency.EUR));

      player.addBonus(new Money(100, Currency.EUR), Money.ZERO);

      when(playerRepository.find(playerId)).thenReturn(player);

      withdrawUseCase.execute(playerId, 100);

      assertEquals(Money.ZERO, player.getBonusWageringLeft(),
              "Le bonus de pari devrait être 0");
      assertEquals(new Money(100, Currency.EUR), player.getBonusBalance());
      verify(playerRepository).save(player);
   }
}