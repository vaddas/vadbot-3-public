package vadbot;

import java.util.ArrayList;
import java.util.Arrays;

import commands.HelpList;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import vadbot.Registry.Command.Category;

public class Registry {

  public static class Command {

    protected class CommandOption {

      protected OptionType type;
      protected String id;
      protected String description;
      protected boolean required;

      public CommandOption(
          OptionType type,
          String id,
          String desc,
          boolean req) {

        this.type = type;
        this.id = id;
        description = desc;
        required = req;

      }

    }

    public enum Category {
      FUN, UTILITY, MODERATOR, MUSIC, LEADERBOARD, MISCELLANEOUS
    };

    protected String id;
    protected String description;
    protected ArrayList<CommandOption> options = new ArrayList<>();
    protected ArrayList<SubcommandData> subcommands = new ArrayList<>();
    protected DefaultMemberPermissions perms;
    public Category category;
    protected boolean hidden;

    public Command(String newID, String desc, Category cat) {

      id = newID;
      description = desc;
      category = cat;
      hidden = false;

    }

    public Command(String newID, String desc, Category cat, boolean hide) {

      id = newID;
      description = desc;
      category = cat;
      hidden = hide;

    }

    public Command
        addOption(OptionType type, String id, String desc, boolean req) {

      options.add(new CommandOption(type, id, desc, req));

      return this;

    }

    public Command addSubcommand(String id, String desc) {

      SubcommandData newCommand = new SubcommandData(id, desc);

      subcommands.add(newCommand);

      return this;

    }

    public Command addSubcommandOption(
        String id,
        OptionType type,
        String optionID,
        String desc,
        boolean req) {

      SubcommandData r = null;

      for (SubcommandData s : subcommands)
        if (s.getName().equals(id))
          r = s;

      r.addOption(type, optionID, desc, req);

      return this;

    }

    public Command setRequiredPerms(Permission... permSet) {

      perms = DefaultMemberPermissions.enabledFor(Arrays.asList(permSet));

      return this;

    }

    public void register(Guild guild) {

      CommandCreateAction newCommand = guild.upsertCommand(id, description);

      if (perms != null)
        newCommand.setDefaultPermissions(perms);

      for (CommandOption o : options)
        newCommand.addOption(o.type, o.id, o.description, o.required);

      for (SubcommandData s : subcommands)
        newCommand.addSubcommands(s);

      newCommand.queue();

    }

    public String getHelpRepresentation() {

      return "**" + id + "**\n" + description;

    }

  }

  public static ArrayList<Command> commands = new ArrayList<>();
  public static ArrayList<Command> betaCommands = new ArrayList<>();

  public static ArrayList<String> serverList = new ArrayList<>();

  public static void registerCommands(JDA jda) {

    serverList.add(ConstantIDs.testServer);

    if (!VadBotMain.isTestBot)
      serverList.add(ConstantIDs.gifSpam);

    // coinflip command
    commands.add(new Command("coinflip", "Flips a single coin.", Category.FUN));

    // flipalot command
    commands.add(
        new Command("flipalot", "Flips many coins.", Category.FUN).addOption(
            OptionType.INTEGER,
            "flips",
            "The number of coins to flip, cannot be more than 1,000,000.",
            true
        )
    );

    // valquote command
    commands.add(
        new Command("valquote", "Quotes a VALORANT character.", Category.FUN)
            .addOption(
                OptionType.STRING,
                "character",
                "A specific character to quote, if you wish.",
                false
            )
    );

    // help command
    commands.add(
        new Command(
            "help", "Get a list of all commands and what they do.",
            Category.UTILITY
        )
    );

    // say command
    commands.add(
        new Command(
            "say", "Get VadBot to say whatever you'd like.", Category.FUN
        ).addOption(
            OptionType.STRING,
            "stuff",
            "The stuff that VadBot should say.",
            true
        )
    );

    // status command
    commands.add(
        new Command(
            "status", "Randomly shuffle VadBot's displayed activity.",
            Category.MISCELLANEOUS
        )
    );

    // avatar command
    commands.add(
        new Command(
            "avatar", "Get the avatar of yourself or a mentioned user.",
            Category.UTILITY
        ).addOption(
            OptionType.USER,
            "person",
            "The person whose avatar you want to get. If left blank, your own avatar will be returned.",
            false
        )
    );

    // kick command
    commands.add(
        new Command("kick", "Kick a member.", Category.MODERATOR)
            .addOption(
                OptionType.USER,
                "member",
                "The member to kick from the server.",
                true
            )
            .setRequiredPerms(Permission.KICK_MEMBERS)
    );

    // ban command
    commands.add(
        new Command("ban", "Ban a member.", Category.MODERATOR)
            .addOption(
                OptionType.USER,
                "member",
                "The member to ban from the server.",
                true
            )
            .setRequiredPerms(Permission.BAN_MEMBERS)
    );

    // penis command
    commands.add(new Command("penis", "Get your penis size.", Category.FUN));
    commands
        .add(new Command("ponis", "Get your ponis size.", Category.FUN, true));

    // choose command
    commands.add(
        new Command(
            "choose", "Choose something random from a given list of things.",
            Category.FUN
        ).addOption(
            OptionType.STRING,
            "options",
            "The list of options to choose from, separated by commas.",
            true
        )
    );

    // join command
    commands.add(
        new Command(
            "join", "Connect me to your current voice channel.", Category.MUSIC
        )
    );

    // leave command
    commands.add(
        new Command(
            "leave", "Disconnect the bot from the current voice channel.",
            Category.MUSIC
        )
    );

    // play command
    commands.add(
        new Command(
            "play", "Play a song from a url or search query in voice channel.",
            Category.MUSIC
        ).addOption(
            OptionType.STRING,
            "query",
            "URL or search query. If playing a song, for best results, add \"audio\" to the query.",
            true
        )
    );

    // pause command
    commands.add(new Command("pause", "Pause the music.", Category.MUSIC));

    // resume command
    commands.add(
        new Command(
            "resume", "Resume the music, if its paused.", Category.MUSIC
        )
    );

    // skip command
    commands.add(
        new Command("skip", "Skip the currently playing track.", Category.MUSIC)
    );

    // shuffle command
    commands.add(
        new Command("shuffle", "Shuffle the music queue.", Category.MUSIC)
    );

    // loop command
    commands.add(
        new Command(
            "loop", "Set the music bot to loop the same track.", Category.MUSIC
        )
    );

    // now playing command
    commands.add(
        new Command(
            "nowplaying", "Show the currently playing track.", Category.MUSIC
        )
    );

    // queue list command
    commands.add(
        new Command(
            "queue", "View the queue of upcoming songs.", Category.MUSIC
        )
    );

    // queue clearing command
    commands.add(
        new Command(
            "clearqueue", "Stops the current song, and clears the queue.",
            Category.MUSIC
        )
    );

    // rank command
    commands.add(
        new Command(
            "rank", "Get you or someone else's rank info in the server.",
            Category.LEADERBOARD
        ).addOption(
            OptionType.USER,
            "member",
            "A member to retrieve the rank of, if you wish.",
            false
        )
    );

    // levels command
    commands.add(
        new Command(
            "levels", "Get a full list of the ranks in the server.",
            Category.LEADERBOARD
        )
    );

    // website command
    commands.add(
        new Command(
            "website", "Get the link to VadBot's website.", Category.UTILITY
        )
    );

    // birthday command
    commands.add(
        new Command(
            "birthday",
            "Various commands related to VadBot's birthday system. Use `/birthday help` to get started.",
            Category.FUN
        ).addSubcommand("set", "Set or update your own birthday.")
            .addSubcommandOption(
                "set",
                OptionType.INTEGER,
                "month",
                "Enter the month as a number from 1 to 12.",
                true
            )
            .addSubcommandOption(
                "set",
                OptionType.INTEGER,
                "day",
                "Enter the month as a number from 1 to 31.",
                true
            )
            .addSubcommandOption(
                "set",
                OptionType.INTEGER,
                "year",
                "Enter the year as a four digit number, i.e. 2004",
                true
            )
            .addSubcommand("get", "See your birthday or someone else's.")
            .addSubcommandOption(
                "get",
                OptionType.USER,
                "person",
                "The person whose birthday you want to see. Leave blank to get your own.",
                false
            )
            .addSubcommand(
                "delete",
                "Remove your birthday from VadBot. VadBot won't ping on your birthday and the data will be erased."
            )
            .addSubcommand(
                "help",
                "Get information about VadBot's birthday functions."
            )
            .addSubcommand("next", "See whose birthday is next.")

    );

    // member profile command
    commands.add(
        new Command(
            "profile", "Get detailed information about someone in the server.",
            Category.UTILITY, false
        ).addOption(
            OptionType.USER,
            "member",
            "The person to get info about, leave blank for info about yourself.",
            false
        )
    );

    // test command
    betaCommands.add(
        new Command("test", "a", Category.MISCELLANEOUS, true)
            .addOption(OptionType.STRING, "d", "penis", false)
    );

    // sql command
    betaCommands.add(
        new Command(
            "sql", "Directly execute an SQL statement.", Category.MISCELLANEOUS,
            true
        ).addOption(
            OptionType.STRING,
            "query",
            "The properly-formatted SQL to run.",
            true
        )
    );

    // birthday nudge
    betaCommands.add(
        new Command(
            "bdaynudge", "Nudge the bot to announce today's birthdays.",
            Category.MISCELLANEOUS, true
        )
    );

    // last fm
    betaCommands.add(
        new Command("login", "login to last fm", Category.MISCELLANEOUS, true)
    );

    betaCommands.add(new Command("fm", "fm", Category.MISCELLANEOUS, true));
    
    betaCommands.add(
        new Command(
            "trends", "Commands for producing charts about listening history.",
            Category.MISCELLANEOUS, true
        ).addSubcommand(
            "track",
            "Listening history for your currently playing track."
        )
            .addSubcommand(
                "artist",
                "Listening history for your currently playing artist."
            )
    );

    // mario 3d world
    betaCommands.add(
        new Command(
            "mgenerate",
            "generate random incomplete mario stage character combo",
            Category.MISCELLANEOUS, true
        )
    );
    betaCommands.add(
        new Command(
            "mcomplete", "finish a level as a character",
            Category.MISCELLANEOUS, true
        ).addOption(
            OptionType.STRING,
            "world",
            "the world. number or capitalized word",
            true
        )
            .addOption(
                OptionType.STRING,
                "stage",
                "the stage number. number or capitalized word or capital letter",
                true
            )
            .addOption(
                OptionType.STRING,
                "character",
                "which character you did",
                true
            )
    );
    betaCommands.add(
        new Command(
            "mstageprogress", "get progress for a stage",
            Category.MISCELLANEOUS, true
        ).addOption(OptionType.STRING, "world", "world", true)
            .addOption(OptionType.STRING, "stage", "stage", true)
    );
    betaCommands.add(
        new Command(
            "mprogress", "get full progress", Category.MISCELLANEOUS, true
        )
    );

    // spotify
    commands.add(
        new Command(
            "spotify", "Get spotify auth link", Category.MISCELLANEOUS, true
        ).addSubcommand("auth", "Link your Spotify to VadBot.")
            .addSubcommand("nowplaying", "Get your currently playing track.")
            .addSubcommand(
                "recs",
                "Get recommendations for songs based on your currently playing track, or a provided track."
            )
            .addSubcommandOption(
                "recs",
                OptionType.STRING,
                "track",
                "The track to search.",
                false
            )
    );

//    testCommand.register(jda.getGuildById(ConstantIDs.testServer));
//    sqlRunner.register(jda.getGuildById(ConstantIDs.testServer));
//    lastFmCommand.register(jda.getGuildById(ConstantIDs.testServer));
//    marioGame.register(jda.getGuildById(ConstantIDs.testServer));
//    marioComplete.register(jda.getGuildById(ConstantIDs.testServer));
//    marioStageProgress.register(jda.getGuildById(ConstantIDs.testServer));
//    marioProgress.register(jda.getGuildById(ConstantIDs.testServer));
//    birthdayNudge.register(jda.getGuildById(ConstantIDs.testServer));

    for (String server : serverList) {

      Guild g = jda.getGuildById(server);
      for (Command c : commands)
        c.register(g);

    }

    Guild test = jda.getGuildById(ConstantIDs.testServer);
    for (Command b : betaCommands) {
      b.register(test);
    }

    ArrayList<Command> helpCommands = new ArrayList<>();
    for (Command c : commands)
      if (!c.hidden)
        helpCommands.add(c);
    HelpList.register(helpCommands);

    System.out.println("Commands have been registered.");

  }

}
