package esgi.fyc.model.playerId;

import java.util.Objects;

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

   public String getValue() {
      return value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof PlayerId playerId)) return false;
      return value.equals(playerId.value);
   }

   @Override
   public int hashCode() {
      return Objects.hash(value);
   }

   @Override
   public String toString() {
      return value;
   }
}