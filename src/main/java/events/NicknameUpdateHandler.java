package events;

import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import vadbot.ConstantIDs;
import vadbot.MemberManager;

public class NicknameUpdateHandler {

  public static void execute(GuildMemberUpdateNicknameEvent event) {

    if (event.getGuild().getId().equals(ConstantIDs.gifSpam)) {

      MemberManager
          .updateSavedNickname(event.getMember(), event.getNewNickname());

    }

  }

}