package esgi.fyc.model.withdrawalLimits;

import esgi.fyc.model.exception.DailyWithdrawalExceededException;
import esgi.fyc.model.exception.MonthlyWithdrawalExceedException;
import esgi.fyc.model.money.Currency;
import esgi.fyc.model.money.Money;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class WithdrawalLimits {
   private static final Money DAILY_LIMIT = new Money(1000, Currency.EUR);
   private static final Money MONTHLY_LIMIT = new Money(5000, Currency.EUR);

   private final Map<LocalDate, Money> dailyWithdrawals;
   private final Map<String, Money> monthlyWithdrawals;

   public WithdrawalLimits() {
      this.dailyWithdrawals = new HashMap<>();
      this.monthlyWithdrawals = new HashMap<>();
   }

   public void recordWithdrawal(Money amount, LocalDate date) {
      Money dailyTotal = dailyWithdrawals.getOrDefault(date, Money.ZERO).add(amount);
      if (dailyTotal.isUpperThan(DAILY_LIMIT)) {
         throw new DailyWithdrawalExceededException(DAILY_LIMIT);
      }

      String monthKey = date.getYear() + "-" + date.getMonthValue();
      Money monthlyTotal = monthlyWithdrawals.getOrDefault(monthKey, Money.ZERO).add(amount);
      if (monthlyTotal.isUpperThan(MONTHLY_LIMIT)) {
         throw new MonthlyWithdrawalExceedException(MONTHLY_LIMIT);
      }

      dailyWithdrawals.put(date, dailyTotal);
      monthlyWithdrawals.put(monthKey, monthlyTotal);
   }

   public Money getDailyWithdrawal(LocalDate date) {
      return dailyWithdrawals.getOrDefault(date, Money.ZERO);
   }
}
