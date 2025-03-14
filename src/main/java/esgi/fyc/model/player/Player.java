package esgi.fyc.model.player;

import esgi.fyc.exception.InsuficientBalanceException;
import esgi.fyc.model.bonusStatus.BonusStatus;
import esgi.fyc.model.kycStatus.KycStatus;
import esgi.fyc.model.money.Money;
import esgi.fyc.model.suspendedstatus.SuspendedStatus;
import esgi.fyc.model.withdrawalLimits.WithdrawalLimits;

import java.time.LocalDate;


public class Player {
   private final PlayerId playerId;
   private Money balance;

   private SuspendedStatus suspendedStatus;
   private KycStatus kycStatus;
   private WithdrawalLimits withdrawalLimits;
   private BonusStatus bonusStatus;

   public Player(PlayerId playerId, Money initialBalance) {
      this.playerId = playerId;
      this.balance = initialBalance;
      this.suspendedStatus = SuspendedStatus.active();
      this.kycStatus = KycStatus.unverified();
      this.withdrawalLimits = new WithdrawalLimits();
      this.bonusStatus = BonusStatus.noBonus();
   }

   public Money getBalance() { return balance; }

   public void withdraw(Money amount) {
      if (balance.isLowerThan(amount)) {
         throw new InsuficientBalanceException();
      }
      balance = balance.subtract(amount);
   }


   public void verifyKyc() {
      this.kycStatus = new KycStatus(true);
   }

   public boolean isKycVerified() {
      return kycStatus.isVerified();
   }

   public void suspend(String reason) {
      this.suspendedStatus = SuspendedStatus.suspended(reason);
   }

   public boolean isSuspended() {
      return suspendedStatus.isSuspended();
   }

   public String getSuspensionReason() {
      return suspendedStatus.getReason();
   }

   public void addBonus(Money bonusAmount, Money wageringRequirement) {
      this.bonusStatus = new BonusStatus(bonusAmount, wageringRequirement);
   }

   public Money getBonusWageringLeft() {
      return bonusStatus.getBonusWageringLeft();
   }

   public void reduceWageringRequirement(Money amountMise) {
      bonusStatus = bonusStatus.reduceWagering(amountMise);
   }

   public void recordWithdrawal(Money amount, LocalDate date) {
      withdrawalLimits.recordWithdrawal(amount, date);
   }

   public Money getDailyWithdrawal(LocalDate date) {
      return withdrawalLimits.getDailyWithdrawal(date);
   }

   public void addToBalance(Money Money) {
      balance = balance.add(Money);
   }

   public Money getBonusBalance() {
      return bonusStatus.getBonusBalance();
   }

   public SuspendedStatus getSuspendedStatus() {
        return suspendedStatus;
   }

    public KycStatus getKycStatus() {
          return kycStatus;
    }

    public WithdrawalLimits getWithdrawalLimits() {
          return withdrawalLimits;
    }

    public BonusStatus getBonusStatus() {
          return bonusStatus;
    }
}