package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import vadbot.ConstantIDs;

public class SayStuff implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    event.getChannel()
        .sendMessage(event.getOption("stuff").getAsString())
        .queue((t) -> {
          event.getHook().sendMessage("as you wish").queue();
        });

    MessageEmbed embed = new EmbedBuilder().setColor(ConstantIDs.color)
        .setDescription(
            "**" + event.getMember().getAsMention() + "** sent *\""
                + event.getOption("stuff").getAsString()
                + "\"* in " + event.getChannel().getAsMention() + " with the /say command."
        )
        .build();

    event.getJDA()
        .getGuildById(ConstantIDs.gifSpam)
        .getTextChannelById(ConstantIDs.gifSpamVadBotLog)
        .sendMessageEmbeds(embed)
        .queue();

  }

  @Override
  public boolean isEphemeral() {
    return true;
  }
  
  

}
