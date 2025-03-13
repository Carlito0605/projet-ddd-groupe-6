package esgi.fyc.model.withdrawalLimits;

import esgi.fyc.use_case.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class WithdrawalLimits {
   private static final BigDecimal DAILY_LIMIT = BigDecimal.valueOf(1000);
   private static final BigDecimal MONTHLY_LIMIT = BigDecimal.valueOf(5000);

   private final Map<LocalDate, BigDecimal> dailyWithdrawals;
   private final Map<String, BigDecimal> monthlyWithdrawals;

   public WithdrawalLimits() {
      this.dailyWithdrawals = new HashMap<>();
      this.monthlyWithdrawals = new HashMap<>();
   }

   public void recordWithdrawal(BigDecimal amount, LocalDate date) {
      BigDecimal dailyTotal = dailyWithdrawals.getOrDefault(date, BigDecimal.ZERO).add(amount);
      if (dailyTotal.compareTo(DAILY_LIMIT) > 0) {
         throw new DomainException("Limite journalière dépassée : " + DAILY_LIMIT + "€");
      }

      String monthKey = date.getYear() + "-" + date.getMonthValue();
      BigDecimal monthlyTotal = monthlyWithdrawals.getOrDefault(monthKey, BigDecimal.ZERO).add(amount);
      if (monthlyTotal.compareTo(MONTHLY_LIMIT) > 0) {
         throw new DomainException("Limite mensuelle dépassée : " + MONTHLY_LIMIT + "€");
      }

      dailyWithdrawals.put(date, dailyTotal);
      monthlyWithdrawals.put(monthKey, monthlyTotal);
   }

   public Map<LocalDate, BigDecimal> getDailyWithdrawals() {
      return dailyWithdrawals;
   }

   public Map<String, BigDecimal> getMonthlyWithdrawals() {
      return monthlyWithdrawals;
   }

   public BigDecimal getDailyWithdrawal(LocalDate date) {
      return dailyWithdrawals.getOrDefault(date, BigDecimal.ZERO);
   }
}
