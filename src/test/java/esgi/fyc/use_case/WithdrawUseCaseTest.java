package esgi.fyc.use_case;

import esgi.fyc.model.player.Player;
import esgi.fyc.model.player.PlayerRepository;
import esgi.fyc.model.playerId.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
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
      PlayerId playerId = PlayerId.of("1234");
      Player player = new Player(playerId, BigDecimal.valueOf(2000));
      player.verifyKyc();
      when(playerRepository.find(playerId)).thenReturn(player);

      withdrawUseCase.execute(playerId, BigDecimal.valueOf(500));

      assertEquals(BigDecimal.valueOf(1500), player.getBalance(),
                   "Le solde devrait être 2000 - 500 = 1500");
      assertEquals(BigDecimal.valueOf(500), player.getDailyWithdrawal(LocalDate.now()));
      verify(playerRepository).save(player);
   }

   @Test
   void testWithdrawSuspendedAccount() {
      PlayerId playerId = PlayerId.of("2345");
      Player player = new Player(playerId, BigDecimal.valueOf(500));
      player.suspend("Fraude détectée");
      when(playerRepository.find(playerId)).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute(playerId, BigDecimal.valueOf(100))
                                       );
      assertTrue(ex.getMessage().contains("Le compte du joueur est suspendu"));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawInsufficientBalance() {
      PlayerId playerId = PlayerId.of("3456");
      Player player = new Player(playerId, BigDecimal.valueOf(100));
      when(playerRepository.find(playerId)).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute(playerId, BigDecimal.valueOf(200))
                                       );
      assertTrue(ex.getMessage().contains("Solde insuffisant"));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawDailyLimitExceeded() {
      PlayerId playerId = PlayerId.of("4567");
      Player player = new Player(playerId, BigDecimal.valueOf(3000));
      player.verifyKyc();
      player.recordWithdrawal(BigDecimal.valueOf(900), LocalDate.now());
      when(playerRepository.find(playerId)).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute(playerId, BigDecimal.valueOf(200))
                                       );
      assertTrue(ex.getMessage().contains("Limite journalière dépassée"));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawMonthlyLimitExceeded() {
      PlayerId playerId = PlayerId.of("5678");
      Player player = new Player(playerId, BigDecimal.valueOf(6000));
      player.verifyKyc();

      LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
      player.recordWithdrawal(BigDecimal.valueOf(1000), firstDayOfMonth);
      player.recordWithdrawal(BigDecimal.valueOf(1000), firstDayOfMonth.plusDays(1));
      player.recordWithdrawal(BigDecimal.valueOf(1000), firstDayOfMonth.plusDays(2));
      player.recordWithdrawal(BigDecimal.valueOf(1000), firstDayOfMonth.plusDays(3));
      player.recordWithdrawal(BigDecimal.valueOf(900), firstDayOfMonth.plusDays(4));

      when(playerRepository.find(playerId)).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute(playerId, BigDecimal.valueOf(200)) // total : 5100€
                                       );

      assertTrue(ex.getMessage().contains("Limite mensuelle dépassée"));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawOver2000WithoutKyc() {
      PlayerId playerId = PlayerId.of("6789");
      Player player = new Player(playerId, BigDecimal.valueOf(3000));
      when(playerRepository.find(playerId)).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute(playerId, BigDecimal.valueOf(2500))
                                       );
      assertTrue(ex.getMessage().contains("Retrait supérieur à 2000€, vérification KYC requise."));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawWithActiveBonus() {
      PlayerId playerId = PlayerId.of("7890");
      Player player = new Player(playerId, BigDecimal.valueOf(500));
      player.addBonus(BigDecimal.valueOf(100), BigDecimal.valueOf(50));
      when(playerRepository.find(playerId)).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute(playerId, BigDecimal.valueOf(100))
                                       );
      assertTrue(ex.getMessage().contains("Bonus actif non complété"));
      verify(playerRepository, never()).save(any(Player.class));
   }
}