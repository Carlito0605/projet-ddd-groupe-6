package esgi.fyc.model.player;

import esgi.fyc.model.playerId.PlayerId;


public interface PlayerRepository {
   Player find(PlayerId playerId);
   void save(Player player);
}
