package esgi.fyc.model.player;


public interface PlayerRepository {
   Player find(PlayerId playerId);
   void save(Player player);
}
