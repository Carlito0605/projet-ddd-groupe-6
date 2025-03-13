package esgi.fyc.model.playerId;

public final class PlayerId {
   private final String value;

   public PlayerId(String value) {
      if (value == null || value.trim().isEmpty()) {
         throw new IllegalArgumentException("L'identifiant joueur ne peut pas être vide.");
      }
      this.value = value;
   }

   public static PlayerId of(String value) {
      return new PlayerId(value);
   }
}