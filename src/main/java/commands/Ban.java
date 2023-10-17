package commands;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Ban implements AbstractVadBotCommand {
  
  @Override
  public void execute(SlashCommandInteractionEvent event) {

    try {
      event.getGuild()
          .ban(event.getOption("member").getAsUser(), 0, TimeUnit.SECONDS)
          .queue((t) -> {
            event.getHook().sendMessage("Member has been banned.").queue();
          });
    } catch (Exception e) {

      event.getHook().sendMessage("You cannot ban that member.").queue();

    }

  }

  @Override
  public boolean isEphemeral() {
    return true;
  }

}
