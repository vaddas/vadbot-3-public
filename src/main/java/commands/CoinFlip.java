package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CoinFlip implements AbstractVadBotCommand {
  
  public void execute(SlashCommandInteractionEvent event) {
    
    int flip = (int) (Math.random() * 2);
    String flipResult = (flip == 0) ? "heads" : "tails";
    event.getHook().sendMessage("It is " + flipResult).queue();
    
  }

}
