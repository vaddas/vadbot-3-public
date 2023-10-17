package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface AbstractVadBotCommand {
  
  public void execute(SlashCommandInteractionEvent event);
  
  public default boolean isEphemeral() {
    return false;
  }
  
}
