package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import vadbot.ConstantIDs;

public class Avatar implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    OptionMapping shord = event.getOption("person");
    EmbedBuilder embed = new EmbedBuilder();
    embed.setColor(ConstantIDs.color);

    if (shord != null) {

      embed.setTitle("Here is " + shord.getAsUser().getName() + "'s avatar.")
          .setImage(shord.getAsUser().getEffectiveAvatarUrl());

    } else {

      embed.setTitle("Here is your avatar.")
          .setImage(event.getUser().getEffectiveAvatarUrl());

    }
    
    event.getHook().sendMessageEmbeds(embed.build()).queue();

  }
  
}
