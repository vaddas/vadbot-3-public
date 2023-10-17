package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import vadbot.VadBotMain;

public class StatusShuffle implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    
    VadBotMain.shuffleStatus(event.getJDA());
    event.getHook().sendMessage("Status has been shuffled.").queue();

  }
  
  @Override
  public boolean isEphemeral() {
    return true;
  }

}
