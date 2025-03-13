package esgi.fyc.use_case;

import esgi.fyc.model.player.Player;
import esgi.fyc.model.player.PlayerRepository;

import java.math.BigDecimal;
import java.time.LocalDate;


class WithdrawUseCase {
   public static final BigDecimal DAILY_LIMIT = BigDecimal.valueOf(1000);
   private static final BigDecimal MONTHLY_LIMIT = BigDecimal.valueOf(5000);
   private static final BigDecimal VERIFICATION_THRESHOLD = BigDecimal.valueOf(2000);

   private PlayerRepository playerRepository;

   void execute(String playerId, BigDecimal amount) {
      Player player = playerRepository.find(playerId);

      extracted(amount, player);

      playerRepository.save(player);
   }

   private void extracted(BigDecimal amount, Player player) {
      if (player.isSuspended()) {
         throw new DomainException("Le compte du joueur est suspendu : " + player.getSuspensionReason());
      }

      if (player.getBalance().compareTo(amount) < 0) {
         throw new DomainException("Solde insuffisant pour effectuer ce retrait.");
      }

      if (amount.compareTo(VERIFICATION_THRESHOLD) > 0 && !player.isKycVerified()) {
         throw new DomainException("Retrait supérieur à 2000€, vérification KYC requise.");
      }

      BigDecimal dailyTotal = player.getDailyWithdrawal(LocalDate.now());
      if (dailyTotal.add(amount).compareTo(DAILY_LIMIT) > 0) {
         throw new DomainException("Limite journalière dépassée : max " + DAILY_LIMIT + "€");
      }

      String yearMonth = LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue());
      BigDecimal monthlyTotal = player.getMonthlyWithdrawal(yearMonth);
      if (monthlyTotal.add(amount).compareTo(MONTHLY_LIMIT) > 0) {
         throw new DomainException("Limite mensuelle dépassée : max " + MONTHLY_LIMIT + "€");
      }

      if (player.getBonusBalance().compareTo(BigDecimal.ZERO) > 0 &&
          player.getBonusWageringLeft().compareTo(BigDecimal.ZERO) > 0) {
         throw new DomainException("Impossible de retirer : bonus actif non complété ("
                                   + player.getBonusWageringLeft() + "€ à miser).");
      }

      player.subtractFromBalance(amount);
      player.recordWithdrawal(amount, LocalDate.now());
   }

}