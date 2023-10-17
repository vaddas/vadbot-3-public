package vadbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import commands.AbstractVadBotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class RankManager {

  private static final int millisecondCooldown = 20000;
  private static final ArrayList<String> progressBars;

  static {

    progressBars = new ArrayList<String>();
    progressBars.addAll(
        Arrays.asList(
            "https://cdn.discordapp.com/attachments/697849954823045283/854914955032002580/32hPOY0r492Xyt65pSq4YAAQIECPwJNE2zHSf99y9sLBwCBAgQKBIQNkVsmggQIEAgR0DY5GipJUCAAIEiAWFTxKaJAAECBHIEhE.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854914974367744000/GDI0fI2zirYwkQIBArgJVVQ2lRz37Fza5dlndBAgQSCwgbBI3wPIECBBYg4CwWUOX7ZEAAQKJBYRN4gZYngABAmsQEDZr6LI9EiB.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854914993668620338/fxpSerkbYnM5eZwIECBwTqKpqOBT137wsY4IECBAIEtA2GSxGUSAAAECKQLCJkVLLQECBAhkCQibLDaDCBAgQCBFQNikaKklQIAA.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915011419832330/hY1Xn1MuBWsjQIDANAJDXn12qXOavdArAQIEkhQYdKnzU8LnapIsCYsiQIDA4QWGfq7m8DPRIwECBAgQCYQdfOTGAECBAgQGCMgb.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915030478356500/dhzNZNqtYslQIDA8QT6fq7meFesMgECBAjMQiDqzc9ZSNgkAQIECCQTEDbJaC1MgAABAnsBYeMsECBAgEBygSTq24VAeKhjwAAAA.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915049716973588/VRBU3iAABAgQIDBWIuvk5tLh5BAgQIECgExA2zgEBAgQIJBcQNsmJLUCAAAEC37nTbhWPSt4WAAAAAElFTkSuQmCC.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915072127139870/7bhXHoKUCAAAAAElFTkSuQmCC.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915117401636884/9CewtbCbQZCUSCAgImwCSIeUCwqbczkwCYxIQNmPq5gBrETYDbIpLItCDQKuw8dfnHjp05FsKmyNvoMsncACBkr8e6jzAPBTWkLY.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915134992154635/xRUkbIprmQ0TCNs8uylTgfnoALC5qD8FiewscBWL3VrJzNRtzu4AAAQJtCmz7uZo2tVRNgAABAnsTSHrzc27sRABAgQIVCkgbKps.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915157636415498/fMPlcTRWwQAQIECKRroYcAQIECBDIKhD15mfWCkxOgAABAsULCJviW2yDBAgQOL6AsDlD1RAgACB4gWAFKCbhU63Zb3AAAAAElFT.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915177736437800/1XgaqXOn9W8rkam4oAAQIEigRqP1dTtLgiAgQIECBQKCrz7VyjiNAgACBYgFhU0ylkAABAgRqBYRNrZzjCBAgQKBY4Bt4qm4VIgV.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915203379363850/QubFCamhqIEhE1R4866WWGT9Xg1doCwubUJ6jvYCwcS0QSFhA2CQ8HKUNEhA2g7gsJjCvgLCZ19vZphMQNtPZ2pnAwQLC5mBCGyQ.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915239958020137/e1AgACBJgT2fV1NEziaJECAAIHjCUQ9Xm88lyZAAECBGoQEDY1TFEPBAgQyFxA2GQIOURIECgBoEfxPpuFR5ZHkAAAAASUVORK5C.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915267188752424/rIm4V8dN8zwAAAABJRU5ErkJggg.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915308225298442/Q3KzgTg4CwGeeU93n02Uud47wWnBWBQQgIm0GM4aBN7PVS53cHPldz0FFYjAABAuMV2PdzNeMVcWYECBAgMAiB0JufghUEwQIECC.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915332934336552/sAAAAASUVORK5CYII.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915355368357898/xcQNrEeRl59dqgzZq2KAIEKBITN8CaGDnXLONzNcO9VRAgQGCRAtHP1SwSy6YJECBAYDqBopOf012OlQgQIECgRgFhU2NX7YkAAQ.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915409530847232/EJhD2ETGGDhFUcQ7nNMU1nhn0zSXKV31IUCAAIE2gbmETXRYLBbxJnZv7BpqxztBAgQ2EFA2GzHEjY7FJGuBAgQaBMQNsKmrUa0E.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915433526067240/Pjpp7dQRIECAQAWB48tXhVHyhuj7vtntdodhGO5SA6cNIbx93EwbQZOHrZoAAQI1BJYYNnHeMXC22218wrlMcYhPNtN0m1KqhgAB.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915451746648104/ExAgQGD2AmOETVxkDJy6ruMdznnKouOdTd9fp5SqIUCAAIGlC4wVNtGhqqr4lfTfv7BZpWjfwIECAwQEDYDsJQSIECAQJ6AsMlzM.png",
            "https://cdn.discordapp.com/attachments/697849954823045283/854915475586416680/oWNlUOAAAECTQLCpolNEwECBAjUCAibGi21BAgQINAkIGya2DQRIECAQI2AsKnRUkuAAAECTQJVYePV5yZjTQQIEOhaoOXVZ5s6u.png"
        )
    );

  }

  public static class RankCommand implements AbstractVadBotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {

      Member member = null;
      MessageCreateBuilder message = new MessageCreateBuilder();

      event.deferReply().queue();
      
      if (event.getOption("member") != null)
        member = event.getOption("member").getAsMember();
      else
        member = event.getMember();

      message.setEmbeds(getRankEmbed(member));
      
      event.getHook().sendMessage(message.build()).queue();

    }

  }

  public static class LevelsCommand implements AbstractVadBotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {

      MessageCreateBuilder message = new MessageCreateBuilder();
      
      event.deferReply().queue();
      
      message.setEmbeds(getLevelsEmbed(event.getJDA()));
      
      event.getHook().sendMessage(message.build()).queue();

    }

  }

  public static void initCooldowns() {

    Database.setAll("member_info", "canGainXP", "T");
    
    System.out.println("Rank cooldowns have been reset.");

  }

  public static void giveCooldown(String memberID) {

    Database.set("member_info", "canGainXP", "member_id", memberID, "F");

    (new Timer()).schedule(new TimerTask() {

      public void run() {
        Database.set("member_info", "canGainXP", "member_id", memberID, "T");
      }

    }, millisecondCooldown);

  }

  public static MessageEmbed getRankEmbed(Member member) {

    String memberID = member.getId();
    EmbedBuilder embed = new EmbedBuilder().setColor(ConstantIDs.color)
        .setTitle(member.getUser().getName() + "'s Rank");

    String totalXPString = Database
        .getByID("member_info", "total_xp", "member_id", memberID);
    Integer totalXP = Integer.valueOf(totalXPString);

    HashMap<String, Integer> rankInfo = calculateLevel(totalXP);
    embed.setDescription(
        "**Level " + rankInfo.get("level") + " | Rank "
            + calculateRank(memberID, member.getJDA()) + " — **"
            + rankInfo.get("progress") + "/" + rankInfo.get("levelrequirement")
            + " XP"
    );
    embed.setFooter(
        member.getUser().getName() + "'s rank | " + totalXP + " Total XP",
        member.getEffectiveAvatarUrl()
    );
    embed.setImage(getProgressBar(rankInfo));

    return embed.build();

  }

  public static MessageEmbed getLevelsEmbed(JDA jda) {

    ArrayList<ArrayList<String>> levelsInfo = Database.getMatrix(
        "member_info",
        "total_xp",
        true,
        "member_id",
        "total_xp",
        "bot"
    );

    String description = "";
    int rank = 1;

    for (ArrayList<String> entry : levelsInfo) {

      if (
        jda.getGuildById(ConstantIDs.gifSpam)
            .getMemberById(entry.get(0)) == null || entry.get(2).equals("T")
      )
        continue;

      description += rank + ". <@!" + entry.get(0) + "> • Level "
          + calculateLevel(Integer.valueOf(entry.get(1))).get("level") + " • "
          + entry.get(1) + " XP\n";

      rank++;

    }

    EmbedBuilder embed = new EmbedBuilder().setColor(ConstantIDs.color)
        .setTitle("Levels in gif spam central")
        .setDescription(description);
    return embed.build();

  }

  public static void addXPtoMember(Member member, MessageChannelUnion channel) {
    
    String memberID = member.getId();

    String canGainXP = Database
        .getByID("member_info", "canGainXP", "member_id", memberID);

    if (canGainXP.equals("T")) {

      String totalXPString = Database
          .getByID("member_info", "total_xp", "member_id", memberID);

      Integer totalXP = Integer.valueOf(totalXPString);

      Integer newTotalXP = totalXP + (new Random()).nextInt(11) + 15;

      totalXPString = newTotalXP.toString();

      Database
          .set("member_info", "total_xp", "member_id", memberID, totalXPString);

      if (!memberID.equals(ConstantIDs.altvaddus))
        giveCooldown(memberID);

      HashMap<String, Integer> oldRankInfo = calculateLevel(totalXP);
      HashMap<String, Integer> newRankInfo = calculateLevel(newTotalXP);

      if (oldRankInfo.get("level") < newRankInfo.get("level")) {
        channel
            .sendMessage(
                member.getAsMention() + " is now level "
                    + newRankInfo.get("level")
            )
            .queue();
      }

    }

  }

  public static HashMap<String, Integer> calculateLevel(int xp) {

    HashMap<String, Integer> map = new HashMap<>();

    int i = 0;
    double xpNeededForI = 0.0;
    double xpNeededForCurrentLevel = 0.0;

    if (xp >= 100.0) {

      while (xp >= xpNeededForI) {

        i++;
        xpNeededForCurrentLevel = xpNeededForI;
        xpNeededForI = (5.0 / 6.0) * i * ((2 * Math.pow(i, 2)) + (27 * i) + 91);

      }

      map.put("level", i - 1);
      map.put("remaining", (int) xpNeededForI - xp);
      map.put("progress", (int) (xp - xpNeededForCurrentLevel));
      map.put(
          "levelrequirement",
          (int) (xpNeededForI - xpNeededForCurrentLevel)
      );

    } else {

      map.put("level", 0);
      map.put("remaining", 100 - xp);
      map.put("progress", xp);
      map.put("levelrequirement", 100);

    }

    return map;

  }

  public static Integer calculateRank(String memberID, JDA jda) {

    ArrayList<ArrayList<String>> sortedRanks = Database.getMatrix(
        "member_info",
        "total_xp",
        true,
        "member_id",
        "bot"
    );

    int rank = 1;

    for (ArrayList<String> entry : sortedRanks) {

      if (entry.get(0).equals(memberID))
        break;

      if (
        jda.getGuildById(ConstantIDs.gifSpam)
            .getMemberById(entry.get(0)) != null && entry.get(1).equals("F")
      )
        rank++;

    }

    return rank;

  }

  public static String getProgressBar(HashMap<String, Integer> rankInfo) {

    return progressBars.get(
        (int) (20 * ((double) rankInfo.get("progress")
            / (double) rankInfo.get("levelrequirement")))
    );

  }

  public static String getProgressBar(int total_xp) {

    return getProgressBar(calculateLevel(total_xp));

  }

}
