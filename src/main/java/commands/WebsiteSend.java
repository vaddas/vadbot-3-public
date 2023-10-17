package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class WebsiteSend implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    // TODO Auto-generated method stub

    event.getHook().sendMessage(
        "Access my website here: https://sites.google.com/view/vadbotcommands/categories?authuser=0"
    ).queue();

  }

}
