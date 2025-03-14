package esgi.fyc.use_case;

import esgi.fyc.model.player.Player;
import esgi.fyc.model.player.PlayerRepository;
import esgi.fyc.model.player.PlayerId;

import java.math.BigDecimal;
import java.time.LocalDate;


class WithdrawUseCase {
   private PlayerRepository playerRepository;

   public void execute(PlayerId playerId, BigDecimal amount) {
      Player player = playerRepository.find(playerId);
      player.withdraw(amount, LocalDate.now());
      playerRepository.save(player);
   }

}