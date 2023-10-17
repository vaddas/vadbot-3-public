package commands;

import java.awt.Color;
import java.time.ZoneId;
import java.util.HashMap;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import vadbot.ConstantIDs;
import vadbot.Database;
import vadbot.RankManager;

public class MemberProfileMaker implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    MessageCreateData message = makeProfile(event);

    event.getHook().sendMessage(message).queue();

  }

  public MessageCreateData makeProfile(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    Member subject = null;
    if (event.getOption("member") != null) {
      subject = event.getOption("member").getAsMember();
    } else {
      subject = event.getMember();
    }

    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("Information about " + subject.getEffectiveName());
    embed.setThumbnail(subject.getEffectiveAvatarUrl());

    Color color = subject.getColor();
    if (color != null) {
      embed.setColor(color);
    } else {
      embed.setColor(ConstantIDs.color);
    }

    String description = "**Username**: ";
    description += subject.getUser().getName();

    description += "\n**Global Display Name:** ";
    description += subject.getUser().getEffectiveName();

    description += "\n**Server Display Name:** ";
    description += subject.getEffectiveName();

    description += "\n";

    if (!subject.getUser().isBot()) {

      description += "\n**Server Rank:** ";
      description += RankManager.calculateRank(subject.getId(), event.getJDA());

      description += "\n**Server Level:** ";
      String totalXPString = Database
          .getByID("member_info", "total_xp", "member_id", subject.getId());
      Integer totalXP = Integer.valueOf(totalXPString);
      HashMap<String, Integer> rankInfo = RankManager.calculateLevel(totalXP);
      description += rankInfo.get("level");

      description += "\n";

      description += "\n**Birthday: **";
      String birthday = Database
          .getByID("member_info", "birthday", "member_id", subject.getId());
      description += (birthday != null)
          ? new BirthdayManager().getFriendlyFormat(birthday)
          : "Not Set";

      description += "\n";

    }

    description += "\n**Joined Discord on:** ";
    description += subject.getUser()
        .getTimeCreated()
        .toLocalDateTime()
        .atZone(ZoneId.of("America/New_York"))
        .format(BirthdayManager.friendlyFormat);

    description += "\n**Joined server on:** ";
    description += subject.getTimeJoined()
        .toLocalDateTime()
        .atZone(ZoneId.of("America/New_York"))
        .format(BirthdayManager.friendlyFormat);

    embed.setDescription(description);

    message.setEmbeds(embed.build());

    return message.build();

  }

}
