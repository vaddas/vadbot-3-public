package events;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import vadbot.ConstantIDs;

public class MemberLeaveHandler {

  public static void execute(GuildMemberRemoveEvent event) {
    
    EmbedBuilder embed = new EmbedBuilder().setTitle("A member has departed.")
        .setDescription(
             event.getUser().getAsMention()
                + " is gone " + ConstantIDs.painwioo
        )
        .setColor(Color.RED);

    String genToSend = "";
    if (event.getGuild().getId().equals(ConstantIDs.gifSpam))
      genToSend = ConstantIDs.gifSpamGeneral;
    else
      genToSend = ConstantIDs.testServerGeneral;
    
    event.getGuild()
        .getTextChannelById(genToSend)
        .sendMessageEmbeds(embed.build())
        .queue((t) -> {
          t.addReaction(Emoji.fromFormatted(ConstantIDs.painwioo)).queue();
        });

  }

}
