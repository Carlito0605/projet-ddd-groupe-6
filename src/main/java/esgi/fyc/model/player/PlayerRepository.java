package esgi.fyc.model.player;


public interface PlayerRepository {
   Player find(String playerId);
   void save(Player player);
}
