package vadbot;

import java.util.concurrent.CompletableFuture;

import buttons.AbstractVadBotButton;
import commands.AbstractVadBotCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import stringmenus.AbstractVadBotStringMenu;

public class VadBotCommandListener extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

    String eventName = event.getName();
    AbstractVadBotCommand cmd = VadBotTranslator.translateCommand(eventName);
    
    event.deferReply(cmd.isEphemeral()).queue();
    
    if (!event.getUser().isBot())
      RankManager.addXPtoMember(event.getMember(), event.getChannel());

    try {
      cmd.execute(event);
    } catch (Exception e) {
      event.getHook().sendMessage("An unexpected error occurred while processing the command.")
          .queue();
      e.printStackTrace();
    }

  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {

    String invokingCommand = event.getButton().getId().split("-")[0];
    AbstractVadBotButton button = VadBotTranslator
        .translateButton(invokingCommand);

    try {
      button.execute(event);
    } catch (Exception e) {
      event.reply("An unexpected error occurred while processing the button.")
          .queue();
      e.printStackTrace();
    }
    
    if (!event.getUser().isBot())
      RankManager.addXPtoMember(event.getMember(), event.getChannel());

  }

  @Override
  public void onStringSelectInteraction(StringSelectInteractionEvent event) {

    String invokingCommand = event.getSelectMenu().getId().split("-")[0];
    AbstractVadBotStringMenu menu = VadBotTranslator
        .translateMenu(invokingCommand);

    try {
      menu.execute(event);
    } catch (Exception e) {
      event
          .reply(
              "An unexpected error occurred while processing the dropdown menu."
          )
          .queue();
      e.printStackTrace();
    }
    
    if (!event.getUser().isBot())
      RankManager.addXPtoMember(event.getMember(), event.getChannel());

  }

}
