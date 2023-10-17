package commands;

import java.util.ArrayList;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import vadbot.Database;

public class Mario implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    MessageCreateData message = null;
    
    switch (event.getName()) {

    case "mgenerate":
      message = generate(event);
      break;
    case "mcomplete":
      message = complete(event);
      break;
    case "mstageprogress":
      message = getStageProgress(event);
      break;
    case "mprogress":
      message = getFullProgress(event);
      break;

    }

    if (message != null)
      event.getHook().sendMessage(message).queue();

  }

  private MessageCreateData
      getFullProgress(SlashCommandInteractionEvent event) {

    MessageCreateData result = null;

    ArrayList<ArrayList<String>> data = Database.getMatrix(
        "3d_world_progress",
        "Mario",
        "Luigi",
        "Peach",
        "Toad",
        "Rosalina"
    );

    int marioTotal = 0, luigiTotal = 0, peachTotal = 0, toadTotal = 0,
        rosalinaTotal = 0;

    for (ArrayList<String> s : data) {

      if (s.get(0).equals("1"))
        marioTotal++;
      if (s.get(1).equals("1"))
        luigiTotal++;
      if (s.get(2).equals("1"))
        peachTotal++;
      if (s.get(3).equals("1"))
        toadTotal++;
      if (s.get(4).equals("1"))
        rosalinaTotal++;

    }

    MessageCreateBuilder builder = new MessageCreateBuilder();
    EmbedBuilder embed = new EmbedBuilder();

    int totalTotal = marioTotal + luigiTotal + peachTotal + toadTotal
        + rosalinaTotal;

    embed.setTitle("Total Progress in Super Mario 3D World");
    embed.setDescription(
        "**Mario**: " + marioTotal + "/110\n" + "**Luigi**: " + luigiTotal
            + "/110\n" + "**Peach**: " + peachTotal + "/110\n" + "**Toad**: "
            + toadTotal + "/110\n" + "**Rosalina**: " + rosalinaTotal
            + "/110\n\n" + "**Total**: " + totalTotal + "/550"
    );

    builder.setEmbeds(embed.build());
    result = builder.build();

    return result;
  }

  private MessageCreateData
      getStageProgress(SlashCommandInteractionEvent event) {
    // TODO Auto-generated method stub

    MessageCreateData result = null;
    MessageCreateBuilder builder = new MessageCreateBuilder();

    ArrayList<ArrayList<String>> data = Database.getMatrix(
        "3d_world_progress",
        "World",
        "Stage",
        "Stage_Name",
        "Mario",
        "Luigi",
        "Peach",
        "Toad",
        "Rosalina"
    );

    String world = event.getOption("world").getAsString();
    String stage = event.getOption("stage").getAsString();

    ArrayList<String> stageData = null;

    for (ArrayList<String> s : data) {

      if (s.get(0).equals(world) && s.get(1).equals(stage)) {
        stageData = s;
        break;
      }

    }

    if (stageData == null) {
      builder.setContent("bruh put in a valid stage");
      return builder.build();
    }

    String m = "❌", l = "❌", p = "❌", t = "❌", r = "❌";

    if (stageData.get(3).equals("1"))
      m = "✅";
    if (stageData.get(4).equals("1"))
      l = "✅";
    if (stageData.get(5).equals("1"))
      p = "✅";
    if (stageData.get(6).equals("1"))
      t = "✅";
    if (stageData.get(7).equals("1"))
      r = "✅";

    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle(
        "Progress for " + translate(world) + "-" + translate(stage) + ": "
            + stageData.get(2)
    );
    embed.setDescription(
        "**Mario**: " + m + "\n" + "**Luigi**: " + l + "\n" + "**Peach**: " + p
            + "\n" + "**Toad**: " + t + "\n" + "**Rosalina**: " + r + "\n"
    );
    
    builder.setEmbeds(embed.build());
    result = builder.build();

    return result;

  }

  public MessageCreateData generate(SlashCommandInteractionEvent event) {

    MessageCreateData result = null;
    MessageCreateBuilder builder = null;

    ArrayList<ArrayList<String>> data = Database.getMatrix(
        "3d_world_progress",
        "World",
        "Stage",
        "Stage_Name",
        "Mario",
        "Luigi",
        "Peach",
        "Toad",
        "Rosalina"
    );

    ArrayList<String> possibilities = null;
    String stageWorld = null;
    String stageNum = null;
    String stageName = null;

    int i;
    for (i = 0; i < 200; i++) {

      possibilities = new ArrayList<String>();

      int stage = (int) (Math.random() * 110);

      stageWorld = data.get(stage).get(0);
      stageNum = data.get(stage).get(1);
      stageName = data.get(stage).get(2);

      System.out.println(data.get(stage));
      if (data.get(stage).get(3).equals("0")) {
        possibilities.add("Mario");
      }
      if (data.get(stage).get(4).equals("0")) {
        possibilities.add("Luigi");
      }
      if (data.get(stage).get(5).equals("0")) {
        possibilities.add("Peach");
      }
      if (data.get(stage).get(6).equals("0")) {
        possibilities.add("Toad");
      }
      if (data.get(stage).get(7).equals("0")) {
        possibilities.add("Rosalina");
      }

      if (possibilities.size() != 0)
        break;

    }

    if (i < 200) {

      String character = possibilities
          .get((int) (Math.random() * possibilities.size()));
      String tWorld = translate(stageWorld);
      String tNum = translate(stageNum);

      EmbedBuilder embed = new EmbedBuilder();
      embed.setDescription(
          "Your next challenge is: Beat **" + tWorld + "-" + tNum + ": "
              + stageName + "** playing as **" + character + "**."
      );

      builder = new MessageCreateBuilder();
      builder.setEmbeds(embed.build());
      return builder.build();

    }

    System.out.println("Rekt");

    return result;

  }

  public MessageCreateData complete(SlashCommandInteractionEvent event) {

    MessageCreateData result = null;
    MessageCreateBuilder builder = new MessageCreateBuilder();

    String character = event.getOption("character").getAsString();

    if (
      !(character.equals("Mario") || character.equals("Luigi")
          || character.equals("Peach") || character.equals("Toad")
          || character.equals("Rosalina"))
    ) {

      builder.setContent("idiot format your character properly");
      return builder.build();

    }

    ArrayList<ArrayList<String>> data = Database.getMatrix(
        "3d_world_progress",
        "id",
        "World",
        "Stage",
        "Stage_Name",
        character
    );

    String world = event.getOption("world").getAsString();
    String stage = event.getOption("stage").getAsString();

    ArrayList<String> stageData = null;

    for (ArrayList<String> s : data) {

      if (s.get(1).equals(world) && s.get(2).equals(stage)) {
        stageData = s;
        break;
      }

    }

    if (stageData == null) {
      builder.setContent("bruh put in a valid stage");
      return builder.build();
    }

    if (stageData.get(4).equals("1")) {
      builder.setContent(
          "Lmao you\'ve already beaten this level with this character"
      );
      return builder.build();
    }

    Database
        .setBit("3d_world_progress", character, "id", stageData.get(0), "1");

    builder.setContent(
        "Successfully logged that **" + translate(world) + "-"
            + translate(stage) + ": " + stageData.get(3)
            + "** was completed by **" + character + "**."
    );
    result = builder.build();

    return result;

  }

  private String translate(String key) {

    String t = key;

    switch (key) {

    case "Castle":
      t = "🏰";
      break;
    case "Mystery House":
      t = "❓";
      break;
    case "Bowser":
      t = "🐲";
      break;
    case "Train":
      t = "🚂";
      break;
    case "Star":
      t = "⭐";
      break;
    case "Mushroom":
      t = "🍄";
      break;
    case "Flower":
      t = "🌻";
      break;
    case "Crown":
      t = "👑";
      break;

    }

    return t;

  }

}
