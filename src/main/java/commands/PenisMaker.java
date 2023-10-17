package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import vadbot.ConstantIDs;

public class PenisMaker implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    MessageEmbed penisEmbed = null;
    if (event.getName().equals("ponis")) {
      penisEmbed = buildAPenis(-1);
    } else {
      int tinyPenis = (int) (Math.random() * 100) + 1;
      if (tinyPenis > 98)
        penisEmbed = buildAPenis(0);
      else if (tinyPenis > 80)
        penisEmbed = buildAPenis((int) (Math.random() * 7) + 6);
      else
        penisEmbed = buildAPenis((int) (Math.random() * 5) + 1);
    }

    event.getHook().sendMessageEmbeds(penisEmbed).queue();

  }

  private MessageEmbed buildAPenis(int penisLength) {

    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("Your penis").setColor(ConstantIDs.color);

    if (penisLength >= 0) {
      
      String protopenis = "8";
      for (int i = 0; i < penisLength; i++)
        protopenis += "=";
      protopenis += "D";
      embed.setDescription("Your penis looks like this\n" + protopenis);
      
    } else {

      embed.setDescription("Your ponis looks like this\n" + ConstantIDs.kenbio);

    }

    return embed.build();

  }

}
