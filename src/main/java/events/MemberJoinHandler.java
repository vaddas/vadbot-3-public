package events;

import java.awt.Color;
import java.util.Arrays;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import vadbot.ConstantIDs;
import vadbot.MemberManager;

public class MemberJoinHandler {

  public static void execute(GuildMemberJoinEvent event) {

    if (event.getGuild().getId().equals(ConstantIDs.gifSpam)) {

      boolean memberIsNew = MemberManager.addMember(event.getMember());
      if (!memberIsNew)
        MemberManager.personalizeMember(event.getMember());

    }

    EmbedBuilder embed = new EmbedBuilder().setTitle("A new member has arrived")
        .setDescription(
            "Everyone give a big welcome to " + event.getMember().getAsMention()
                + " I guess"
        )
        .setColor(Color.GREEN);

    String genToSend = "";
    if (event.getGuild().getId().equals(ConstantIDs.gifSpam)) {
      genToSend = ConstantIDs.gifSpamGeneral;
      event.getGuild()
          .modifyMemberRoles(
              event.getMember(),
              Arrays.asList(
                  event.getGuild().getRoleById(ConstantIDs.someoneRole),
                  event.getGuild().getRoleById(ConstantIDs.anyoneRole)
              ),
              Arrays.asList()
          )
          .queue();
    } else {
      genToSend = ConstantIDs.testServerGeneral;
    }

    event.getGuild()
        .getTextChannelById(genToSend)
        .sendMessageEmbeds(embed.build())
        .queue((t) -> {
          t.addReaction(Emoji.fromFormatted(ConstantIDs.letsgo)).queue();
        });

  }

}
