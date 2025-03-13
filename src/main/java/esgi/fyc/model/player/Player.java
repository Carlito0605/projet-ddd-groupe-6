package esgi.fyc.model.player;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class Player {
   private final String playerId;
   private BigDecimal balance;
   private boolean suspended;
   private String suspensionReason;
   private final Map<LocalDate, BigDecimal> dailyWithdrawals;
   private final Map<String, BigDecimal> monthlyWithdrawals;
   private boolean kycVerified;
   private BigDecimal bonusBalance;
   private BigDecimal bonusWageringLeft;

   public Player(String playerId, BigDecimal initialBalance) {
      this.playerId = playerId;
      this.balance = initialBalance;
      this.suspended = false;
      this.suspensionReason = null;

      this.dailyWithdrawals = new HashMap<>();
      this.monthlyWithdrawals = new HashMap<>();

      this.kycVerified = false;
      this.bonusBalance = BigDecimal.ZERO;
      this.bonusWageringLeft = BigDecimal.ZERO;
   }

   public String getPlayerId() { return playerId; }
   public BigDecimal getBalance() { return balance; }
   public boolean isSuspended() { return suspended; }
   public String getSuspensionReason() { return suspensionReason; }
   public boolean isKycVerified() { return kycVerified; }
   public BigDecimal getBonusBalance() { return bonusBalance; }
   public BigDecimal getBonusWageringLeft() { return bonusWageringLeft; }

   public void verifyKyc() {
      this.kycVerified = true;
   }

   public void suspend(String reason) {
      this.suspended = true;
      this.suspensionReason = reason;
   }

   public void addBonus(BigDecimal bonusAmount, BigDecimal wageringRequirement) {
      this.bonusBalance = bonusAmount;
      this.bonusWageringLeft = wageringRequirement;
   }

   public void addToBalance(BigDecimal amount) {
      this.balance = this.balance.add(amount);
   }

   public void subtractFromBalance(BigDecimal amount) {
      this.balance = this.balance.subtract(amount);
   }

   public void reduceWageringRequirement(BigDecimal amountMise) {
      this.bonusWageringLeft = this.bonusWageringLeft.subtract(amountMise);
      if (this.bonusWageringLeft.compareTo(BigDecimal.ZERO) < 0) {
         this.bonusWageringLeft = BigDecimal.ZERO;
      }
   }
   public void recordWithdrawal(BigDecimal amount, LocalDate date) {
      this.dailyWithdrawals.putIfAbsent(date, BigDecimal.ZERO);
      BigDecimal dailyTotal = this.dailyWithdrawals.get(date).add(amount);
      this.dailyWithdrawals.put(date, dailyTotal);

      String yearMonth = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
      this.monthlyWithdrawals.putIfAbsent(yearMonth, BigDecimal.ZERO);
      BigDecimal monthlyTotal = this.monthlyWithdrawals.get(yearMonth).add(amount);
      this.monthlyWithdrawals.put(yearMonth, monthlyTotal);
   }

   public BigDecimal getDailyWithdrawal(LocalDate date) {
      return dailyWithdrawals.getOrDefault(date, BigDecimal.ZERO);
   }

   public BigDecimal getMonthlyWithdrawal(String yearMonth) {
      return monthlyWithdrawals.getOrDefault(yearMonth, BigDecimal.ZERO);
   }
}