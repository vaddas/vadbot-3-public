package events;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import vadbot.ConstantIDs;
import vadbot.MemberManager;

public class RoleUpdateHandler {

  public static void roleAdd(GuildMemberRoleAddEvent event) {
    
    if (event.getGuild().getId().equals(ConstantIDs.gifSpam)) {

      MemberManager.updateSavedRoles(event.getMember(), event.getRoles(), true);

    }

  }

  public static void roleRemove(GuildMemberRoleRemoveEvent event) {

    if (
      event.getGuild().getId().equals(ConstantIDs.gifSpam)
    ) {

      MemberManager.updateSavedRoles(event.getMember(), event.getRoles(), false);

    }

  }

}
