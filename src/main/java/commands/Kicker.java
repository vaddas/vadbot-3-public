package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Kicker implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    try {
      event.getGuild()
          .kick(event.getOption("member").getAsUser())
          .queue((t) -> {
            event.getHook().sendMessage("Member has been kicked.").queue();
          });
    } catch (Exception e) {

      event.getHook().sendMessage("You cannot kick that member.").queue();

    }

  }

  @Override
  public boolean isEphemeral() {
    return true;
  }
  
  

}
