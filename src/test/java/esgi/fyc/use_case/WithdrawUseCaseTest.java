package esgi.fyc.use_case;

import esgi.fyc.model.player.Player;
import esgi.fyc.model.player.PlayerRepository;
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
   private PlayerRepository playerRepository;

   @InjectMocks
   private WithdrawUseCase withdrawUseCase;

   @BeforeEach
   void setUp() {
      MockitoAnnotations.openMocks(this);
   }

   @Test
   void testWithdrawValid() {
      Player player = new Player("1234", BigDecimal.valueOf(2000));
      player.verifyKyc();
      when(playerRepository.find("1234")).thenReturn(player);


      withdrawUseCase.execute("1234", BigDecimal.valueOf(500));


      assertEquals(BigDecimal.valueOf(1500), player.getBalance(),
                   "Le solde devrait être 2000 - 500 = 1500");

      assertEquals(BigDecimal.valueOf(500), player.getDailyWithdrawal(LocalDate.now()),
                   "Le retrait quotidien doit être de 500");

      verify(playerRepository).save(player);
   }

   @Test
   void testWithdrawSuspendedAccount() {

      Player player = new Player("2345", BigDecimal.valueOf(500));
      player.suspend("Fraude détectée");
      when(playerRepository.find("2345")).thenReturn(player);


      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute("2345", BigDecimal.valueOf(100))
                                       );
      assertTrue(ex.getMessage().contains("Le compte du joueur est suspendu"));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawInsufficientBalance() {

      Player player = new Player("3456", BigDecimal.valueOf(100));
      when(playerRepository.find("3456")).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute("3456", BigDecimal.valueOf(200))
                                       );
      assertTrue(ex.getMessage().contains("Solde insuffisant"));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawDailyLimitExceeded() {

      Player player = new Player("4567", BigDecimal.valueOf(3000));
      player.verifyKyc();

      player.recordWithdrawal(BigDecimal.valueOf(900), LocalDate.now());
      when(playerRepository.find("4567")).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute("4567", BigDecimal.valueOf(200))
                                       );
      assertTrue(ex.getMessage().contains("Limite journalière dépassée"));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawMonthlyLimitExceeded() {

      Player player = new Player("5678", BigDecimal.valueOf(6000));
      player.verifyKyc();

      LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
      player.recordWithdrawal(BigDecimal.valueOf(3000), firstDayOfMonth);
      player.recordWithdrawal(BigDecimal.valueOf(1900), firstDayOfMonth.plusDays(5));

      when(playerRepository.find("5678")).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute("5678", BigDecimal.valueOf(200))
                                       );
      assertTrue(ex.getMessage().contains("Limite mensuelle dépassée"),
                 "Devrait échouer car le retrait dépasse la limite mensuelle");

      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawOver2000WithoutKyc() {
      Player player = new Player("6789", BigDecimal.valueOf(3000));
      when(playerRepository.find("6789")).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute("6789", BigDecimal.valueOf(2500))
                                       );

      assertTrue(ex.getMessage().contains("Retrait supérieur à 2000€, vérification KYC requise."));
      verify(playerRepository, never()).save(any(Player.class));
   }

   @Test
   void testWithdrawWithActiveBonus() {

      Player player = new Player("7890", BigDecimal.valueOf(500));

      player.addBonus(BigDecimal.valueOf(100), BigDecimal.valueOf(50));
      when(playerRepository.find("7890")).thenReturn(player);

      DomainException ex = assertThrows(
            DomainException.class,
            () -> withdrawUseCase.execute("7890", BigDecimal.valueOf(100))
                                       );
      assertTrue(ex.getMessage().contains("Impossible de retirer : bonus actif non complété"));
      verify(playerRepository, never()).save(any(Player.class));
   }
}