package commands;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import vadbot.ConstantIDs;
import vadbot.Database;

public class BirthdayManager implements AbstractVadBotCommand {

  public static DateTimeFormatter storedFormat = DateTimeFormatter
      .ofPattern("M/d/y");;
  public static DateTimeFormatter friendlyFormat = DateTimeFormatter
      .ofPattern("MMMM d, y");

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    MessageCreateData message = null;
    
    if (event.getName().equals("bdaynudge")) {

      System.out.println("Forcing birthday script to run.");
      sendBirthdayMessage(event.getJDA());
      message = new MessageCreateBuilder().setContent("Ok").build();
      
    } else {

      switch (event.getSubcommandName()) {

      case "set":
        message = set(event);
        break;
      case "get":
        message = get(event);
        break;
      case "delete":
        message = delete(event);
        break;
      case "help":
        message = info(event);
        break;
      case "next":
        message = nextBirthday(event);
        break;

      }

    }

    if (message != null)
      event.getHook().sendMessage(message).queue();

  }

  public void sendBirthdayMessage(JDA jda) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    ArrayList<ArrayList<String>> birthdays = Database
        .getMatrix("member_info", "member_id", "birthday");

    ArrayList<ArrayList<String>> filtered = new ArrayList<>();
    for (ArrayList<String> b : birthdays) {
      if (b.get(1) != null) {
        filtered.add(b);
      }
    }

    ArrayList<ArrayList<String>> birthdayBoys = new ArrayList<>();

    for (ArrayList<String> b : filtered) {
      LocalDate birthdate = LocalDate.parse(b.get(1), storedFormat);
      if (LocalDate.now().withYear(birthdate.getYear()).isEqual(birthdate)) {
        birthdayBoys.add(b);
      }
    }

    if (birthdayBoys.size() == 1) {
      message.setContent(
          "@everyone Today is **"
              + jda.getGuildById(ConstantIDs.gifSpam)
                  .getMemberById(birthdayBoys.get(0).get(0))
                  .getEffectiveName()
              + "**'s birthday! They turn **"
              + getAge(birthdayBoys.get(0).get(1))
              + "** years old today. Happy birthday, "
              + jda.getGuildById(ConstantIDs.gifSpam)
                  .getMemberById(birthdayBoys.get(0).get(0))
                  .getAsMention()
              + "!"
      );
    } else if (birthdayBoys.size() > 1) {

      String messageString = "@everyone We have many birthdays today! **";
      for (int i = 0; i < birthdayBoys.size() - 2; i++) {
        messageString = messageString
            + jda.getGuildById(ConstantIDs.gifSpam)
                .getMemberById(birthdayBoys.get(i).get(0))
                .getEffectiveName()
            + "** is turning **" + getAge(birthdayBoys.get(i).get(1)) + ", ";
      }
      messageString = messageString
          + jda.getGuildById(ConstantIDs.gifSpam)
              .getMemberById(birthdayBoys.get(birthdayBoys.size() - 2).get(0))
              .getEffectiveName()
          + "** is turning **"
          + getAge(birthdayBoys.get(birthdayBoys.size() - 2).get(1))
          + "**, and **"
          + jda.getGuildById(ConstantIDs.gifSpam)
              .getMemberById(birthdayBoys.get(birthdayBoys.size() - 1).get(0))
              .getEffectiveName()
          + "** is turning **"
          + getAge(birthdayBoys.get(birthdayBoys.size() - 1).get(1))
          + "** today. Happy birthday, ";
      for (int i = 0; i < birthdayBoys.size(); i++) {
        messageString = messageString + jda.getGuildById(ConstantIDs.gifSpam)
            .getMemberById(birthdayBoys.get(i).get(0))
            .getAsMention() + " ";
      }
      messageString = messageString + "!";

      message.setContent(messageString);

    }

    if (birthdayBoys.size() > 0) {
      jda.getGuildById(ConstantIDs.gifSpam)
          .getTextChannelById(ConstantIDs.gifSpamGeneral)
          .sendMessage(message.build())
          .queue();
    }

  }

  public MessageCreateData nextBirthday(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    ArrayList<ArrayList<String>> birthdays = Database
        .getMatrix("member_info", "member_id", "birthday");

    ArrayList<ArrayList<String>> filtered = new ArrayList<>();

    for (ArrayList<String> b : birthdays) {
      if (b.get(1) != null) {
        filtered.add(b);
      }
    }

    for (ArrayList<String> f : filtered)
      f.add(Integer.toString(daysTilBirthday(f.get(1))));

    int closestDays = 400;

    for (ArrayList<String> f : filtered)
      closestDays = Math.min(closestDays, Integer.parseInt(f.get(2)));

    ArrayList<ArrayList<String>> closestBirthdayBoys = new ArrayList<>();
    for (ArrayList<String> f : filtered)
      if (closestDays == Integer.parseInt(f.get(2)))
        closestBirthdayBoys.add(f);

    if (closestBirthdayBoys.size() == 1) {
      message.setContent(
          "**" + event.getJDA()
              .getGuildById(ConstantIDs.gifSpam)
              .getMemberById(closestBirthdayBoys.get(0).get(0))
              .getEffectiveName()
              + "** has the closest upcoming birthday, in **" + closestDays
              + "** days on **"
              + getFriendlyFormat(closestBirthdayBoys.get(0).get(1)) + "**."
      );
    } else if (closestBirthdayBoys.size() > 1) {

      String messageString = "**";
      for (int i = 0; i < closestBirthdayBoys.size() - 2; i++) {
        messageString = messageString + event.getJDA()
            .getGuildById(ConstantIDs.gifSpam)
            .getMemberById(closestBirthdayBoys.get(i).get(0))
            .getEffectiveName() + ", ";
      }
      messageString = messageString
          + event.getJDA()
              .getGuildById(ConstantIDs.gifSpam)
              .getMemberById(
                  closestBirthdayBoys.get(closestBirthdayBoys.size() - 2).get(0)
              )
              .getEffectiveName()
          + "** and **"
          + event.getJDA()
              .getGuildById(ConstantIDs.gifSpam)
              .getMemberById(
                  closestBirthdayBoys.get(closestBirthdayBoys.size() - 1).get(0)
              )
              .getEffectiveName()
          + "** share the closest upcoming birthday, which is in **"
          + closestDays + "** days on **"
          + getFriendlyFormat(closestBirthdayBoys.get(0).get(1)) + "**.";

      message.setContent(messageString);

    }

    return message.build();

  }

  public MessageCreateData set(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    int month = event.getOption("month").getAsInt();
    int day = event.getOption("day").getAsInt();
    int year = event.getOption("year").getAsInt();

    LocalDate date = null;
    String dateString = null;
    boolean dateIsValid;
    String friendlyFormat = null;

    try {
      DateTimeFormatter format = DateTimeFormatter.ofPattern("M/d/y");
      date = LocalDate.parse((month + "/" + day + "/" + year), format);
      dateString = date.format(format);
      format = DateTimeFormatter.ofPattern("MMMM d, y");
      friendlyFormat = date.format(format);
      dateIsValid = true;
    } catch (DateTimeParseException e) {
      dateIsValid = false;
    }

    if (year < 1900) {
      message.setContent("I strongly doubt that you were born before 1900.");
    } else if (LocalDate.now().isBefore(date)) {
      message.setContent("Bruh you aren't born yet??? Rejected.");
    } else if (!dateIsValid) {
      message.setContent("The date you entered is invalid. Please try again.");
    } else {
      Database.set(
          "member_info",
          "birthday",
          "member_id",
          event.getUser().getId(),
          dateString
      );
      message.setContent(
          "Successfully set your birthday as **" + friendlyFormat
              + "**. If this is incorrect, please try again using `/birthday set`."
      );
    }

    return message.build();

  }

  public MessageCreateData get(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    User birthdayBoy;

    try {
      birthdayBoy = event.getOption("person").getAsUser();
    } catch (NullPointerException e) {
      birthdayBoy = event.getUser();
    }

    String birthday = Database
        .getByID("member_info", "birthday", "member_id", birthdayBoy.getId());

    if (birthday == null)
      message.setContent(
          "**" + birthdayBoy.getEffectiveName()
              + "** has not set up their birthday yet."
      );
    else {
      message.setContent(
          "**" + birthdayBoy.getEffectiveName() + "** will turn **"
              + (getAge(birthday) + 1) + "** on **" + getNextBirthday(birthday)
              + "**, which is in " + daysTilBirthday(birthday)
              + " days. They were born in **" + LocalDate.parse(birthday, storedFormat).getYear()
              + "**."
      );
    }

    return message.build();

  }

  public MessageCreateData delete(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    String birthday = Database.getByID(
        "member_info",
        "birthday",
        "member_id",
        event.getUser().getId()
    );

    if (birthday == null)
      message
          .setContent("You do not currently have a saved birthday to delete.");
    else {
      Database.setBit(
          "member_info",
          "birthday",
          "member_id",
          event.getUser().getId(),
          "NULL"
      );
      message.setContent("Successfully removed your birthday.");
    }

    return message.build();

  }

  public MessageCreateData info(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("VadBot Birthday Info");
    String info = "VadBot can store your birthday, and it will @ everyone once "
        + "your birthday comes and wish you a happy birthday! It can also let "
        + "you know how many days are until your birthday, and it lets you "
        + "know when other people's birthdays are too.\n\nTo get started, "
        + "use `/birthday set` to set your birthday.\n\nYou can also use "
        + "`/birthday get` to see your birthday or anyone else's, and how "
        + "many days until their birthday.\n\nIf you want to delete your "
        + "birthday, you can do so using `/birthday delete`.\n\nTo see whose "
        + "birthday is next, use `/birthday next`.\n\n";
    embed.setDescription(info);

    message.setEmbeds(embed.build());

    return message.build();

  }

  private int getAge(String birthday) {

    return getAge(LocalDate.parse(birthday, storedFormat));

  }

  private int getAge(LocalDate birthday) {

    return (int) birthday.until(LocalDate.now(), ChronoUnit.YEARS);

  }

  public String getFriendlyFormat(String birthday) {

    return getFriendlyFormat(LocalDate.parse(birthday, storedFormat));

  }

  public String getFriendlyFormat(LocalDate birthday) {

    return birthday.format(friendlyFormat);

  }

  private String getNextBirthday(String birthday) {

    return getNextBirthday(LocalDate.parse(birthday, storedFormat));

  }

  private String getNextBirthday(LocalDate birthday) {

    return birthday.plusYears(getAge(birthday) + 1).format(friendlyFormat);

  }

  private int daysTilBirthday(String birthday) {

    return daysTilBirthday(LocalDate.parse(birthday, storedFormat));

  }

  private int daysTilBirthday(LocalDate birthday) {

    return (int) LocalDate.now()
        .until(
            LocalDate.parse(getNextBirthday(birthday), friendlyFormat),
            ChronoUnit.DAYS
        );

  }

}
