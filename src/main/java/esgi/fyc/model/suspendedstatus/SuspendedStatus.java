package esgi.fyc.model.suspendedstatus;

import esgi.fyc.model.exception.SuspendedPlayerException;

public class SuspendedStatus {
   private final boolean isSuspended;
   private final String reason;

   public SuspendedStatus(boolean isSuspended, String reason) {
      this.isSuspended = isSuspended;
      this.reason = reason;
   }

   public void verifyNotSuspended() {
      if (isSuspended)
         throw new SuspendedPlayerException(reason);
   }

   public boolean isSuspended() {
      return isSuspended;
   }

   public String getReason() {
      return reason;
   }

   public static SuspendedStatus active() {
      return new SuspendedStatus(false, null);
   }

   public static SuspendedStatus suspended(String reason) {
      return new SuspendedStatus(true, reason);
   }
}