package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class FlipaLot implements AbstractVadBotCommand {

  public void execute(SlashCommandInteractionEvent event) {

    OptionMapping flips = event.getOption("flips");
    String flipaLotResult;
    try {
      flipaLotResult = flipaLot(flips.getAsInt());
    } catch (ArithmeticException e) {
      flipaLotResult = "Coins cannot be flipped more than a million times.";
    }

    event.getHook().sendMessage(flipaLotResult).queue();

  }

  private String flipaLot(int flips) {

    String result;

    if (flips < 1) {
      result = "Coins cannot be flipped less than once.";
    } else if (flips > 1000000) {
      result = "Coins cannot be flipped more than a million times.";
    } else {

      int headCount = 0, tailCount = 0;

      for (int i = 0; i < flips; i++) {

        int flip = (int) (Math.random() * 2);

        if (flip == 0)
          headCount++;
        else
          tailCount++;

      }

      result = "After flipping " + flips + " times, it landed on heads "
          + headCount + " times and landed on tails " + tailCount
          + " times. Gg";

    }

    return result;

  }

}
