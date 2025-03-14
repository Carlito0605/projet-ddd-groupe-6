package esgi.fyc.use_case;

import esgi.fyc.model.money.Currency;
import esgi.fyc.model.money.Money;
import esgi.fyc.model.player.Player;
import esgi.fyc.model.player.PlayerRepository;
import esgi.fyc.model.player.PlayerId;

import java.math.BigDecimal;
import java.time.LocalDate;


class WithdrawUseCase {
   private PlayerRepository playerRepository;

   public void execute(String playerId, int amount) {
      Player player = playerRepository.find(playerId);
      Money money = new Money(amount, Currency.EUR);
      player.withdraw(money, LocalDate.now());
      playerRepository.save(player);
   }
}