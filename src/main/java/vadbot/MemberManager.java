package vadbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class MemberManager {

  // member info: member_id, nickname, total_xp, canGainXP, bot

  public static boolean addMember(Member member) {

    if (
      !Database.containsUniqueKey("member_info", "member_id", member.getId())
    ) {

      String isBot = member.getUser().isBot() ? "T" : "F";
      String values = "('" + member.getId() + "', '" + member.getEffectiveName()
          + "', 0, 'T', '" + isBot + "')";
      Database.insert(
          "member_info",
          "(member_id, nickname, total_xp, canGainXP, bot)",
          values
      );

      System.out.println(
          "New member " + member.getEffectiveName()
              + " was added to the database."
      );
      return true;

    } else {

      return false;

    }

  }

  public static void updateSaveData(Member member) {

    boolean memberIsNew = addMember(member);

    if (!memberIsNew) {
      updateSavedNickname(member, member.getEffectiveName());

      List<Role> roles = member.getRoles();
      ArrayList<String> roleIDs = new ArrayList<>();
      for (Role r : roles) {
        roleIDs.add(r.getId());
      }
      ArrayList<String> savedRoles = Database
          .getAllMatches("role_info", "role_id", "member_id", member.getId());

      ArrayList<String> rolesToSave = new ArrayList<>(roleIDs);
      rolesToSave.removeAll(savedRoles);

      ArrayList<String> rolesToDelete = new ArrayList<>(savedRoles);
      rolesToDelete.removeAll(roleIDs);

      for (String r : rolesToDelete) {

        removeRoleFromMemberData(member, r);

      }

      for (String r : rolesToSave) {

        addRoleToMemberData(member, r);

      }

    }

  }

  public static void
      updateSavedRoles(Member member, List<Role> roles, boolean add) {

    for (Role r : roles) {

      if (add)
        addRoleToMemberData(member, r);
      else
        removeRoleFromMemberData(member, r);

    }

  }

  public static void updateSavedNickname(Member member, String newNickname) {

    if (
      !Database.getByID("member_info", "nickname", "member_id", member.getId())
          .equals(newNickname)
    ) {
      Database.set(
          "member_info",
          "nickname",
          "member_id",
          member.getId(),
          newNickname
      );

      System.out.println(
          "Member " + member.getUser().getName() + "'s nickname was updated to "
              + newNickname
      );

    }

  }

  public static void init(JDA jda) {

    List<Member> members = jda.getGuildById(ConstantIDs.gifSpam).getMembers();

    for (Member m : members)
      updateSaveData(m);

    System.out.println("Member info in database updated.");

  }

  public static void personalizeMember(Member member) {

    String nickname = Database
        .getByID("member_info", "nickname", "member_id", member.getId());

    member.modifyNickname(nickname).queue();

    ArrayList<String> roles = Database
        .getAllMatches("role_info", "role_id", "member_id", member.getId());

    for (String r : roles) {
      member.getGuild()
          .addRoleToMember(member, member.getGuild().getRoleById(r))
          .queue();
      System.out.println(
          "Reassigned " + member.getGuild().getRoleById(r).getName() + " to "
              + member.getUser().getName() + "."
      );
    }

  }

  private static void addRoleToMemberData(Member member, String roleID) {

    if (
      !Database.containsEntry(
          "role_info",
          Arrays.asList("member_id", "role_id"),
          Arrays.asList(member.getId(), roleID)
      )
    ) {
      boolean done = Database.insert(
          "role_info",
          "(member_id, role_id)",
          "(" + member.getId() + ", " + roleID + ")"
      );
      System.out.println(
          done
              ? "Added " + member.getGuild().getRoleById(roleID).getName()
                  + " to " + member.getUser().getName() + "'s saved roles."
              : "Could not add "
                  + member.getGuild().getRoleById(roleID).getName() + " to "
                  + member.getUser().getName() + "'s saved roles."
      );
    }

  }

  private static void addRoleToMemberData(Member member, Role role) {

    addRoleToMemberData(member, role.getId());

  }

  private static void removeRoleFromMemberData(Member member, String roleID) {

    boolean done = Database.deleteEntry(
        "role_info",
        "member_id",
        member.getId(),
        "role_id",
        roleID
    );

    try {
      System.out.println(
          done
              ? "Removed " + member.getGuild().getRoleById(roleID).getName()
                  + " from " + member.getUser().getName() + "'s saved roles."
              : "Could not remove "
                  + member.getGuild().getRoleById(roleID).getName() + " from "
                  + member.getUser().getName() + "'s saved roles."
      );
    } catch (Exception e) {

      System.out.println("There was an exception: " + e.getMessage() + "\n\n"
          + "This usually is a null pointer exception.");
      
    }

  }

  private static void removeRoleFromMemberData(Member member, Role role) {

    removeRoleFromMemberData(member, role.getId());

  }

}
