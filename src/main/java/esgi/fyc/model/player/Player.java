package esgi.fyc.model.player;

import esgi.fyc.model.bonusStatus.BonusStatus;
import esgi.fyc.model.kycStatus.KycStatus;
import esgi.fyc.model.playerId.PlayerId;
import esgi.fyc.model.suspendedstatus.SuspendedStatus;
import esgi.fyc.model.withdrawalLimits.WithdrawalLimits;
import esgi.fyc.use_case.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;


public class Player {
   private final PlayerId playerId;
   private BigDecimal balance;

   private SuspendedStatus suspendedStatus;
   private KycStatus kycStatus;
   private WithdrawalLimits withdrawalLimits;
   private BonusStatus bonusStatus;

   public Player(PlayerId playerId, BigDecimal initialBalance) {
      this.playerId = playerId;
      this.balance = initialBalance;
      this.suspendedStatus = SuspendedStatus.active();
      this.kycStatus = KycStatus.unverified();
      this.withdrawalLimits = new WithdrawalLimits();
      this.bonusStatus = BonusStatus.noBonus();
   }

   public PlayerId getPlayerId() { return playerId; }
   public BigDecimal getBalance() { return balance; }
   public SuspendedStatus suspendedStatus() { return suspendedStatus; }
   public KycStatus kycStatus() { return kycStatus; }
   public WithdrawalLimits withdrawalLimits() { return withdrawalLimits; }
   public BonusStatus bonusStatus() { return bonusStatus; }

   public void withdraw(BigDecimal amount, LocalDate date) {
      suspendedStatus.verifyNotSuspended();
      kycStatus.verify(amount);
      bonusStatus.verifyBonusConditions();
      withdrawalLimits.recordWithdrawal(amount, date);

      if (balance.compareTo(amount) < 0) {
         throw new DomainException("Solde insuffisant.");
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

   public void addBonus(BigDecimal bonusAmount, BigDecimal wageringRequirement) {
      this.bonusStatus = new BonusStatus(bonusAmount, wageringRequirement);
   }

   public BigDecimal getBonusWageringLeft() {
      return bonusStatus.getBonusWageringLeft();
   }

   public void reduceWageringRequirement(BigDecimal amountMise) {
      bonusStatus = bonusStatus.reduceWagering(amountMise);
   }

   public void recordWithdrawal(BigDecimal amount, LocalDate date) {
      withdrawalLimits.recordWithdrawal(amount, date);
   }

   public BigDecimal getDailyWithdrawal(LocalDate date) {
      return withdrawalLimits.getDailyWithdrawal(date);
   }

   public void addToBalance(BigDecimal bigDecimal) {
      balance = balance.add(bigDecimal);
   }

   public BigDecimal getBonusBalance() {
      return bonusStatus.getBonusBalance();
   }

}