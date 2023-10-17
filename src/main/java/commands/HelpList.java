package commands;

import java.util.ArrayList;
import java.util.HashMap;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import vadbot.ConstantIDs;
import vadbot.Registry.Command;
import vadbot.Registry.Command.Category;

public class HelpList implements AbstractVadBotCommand {

  private static final int commandsPerPage = 6;
  private static HashMap<Category, ArrayList<HelpPage>> sections = new HashMap<>();

  private static class HelpPage {

    ArrayList<Command> commandsOnPage = new ArrayList<>();

    public String toString() {

      String result = "";

      for (Command c : commandsOnPage) {

        result = result + c.getHelpRepresentation() + "\n\n";

      }

      return result;

    }

  }

  public static void register(ArrayList<Command> commands) {

    for (Command c : commands) {

      sections.put(c.category, new ArrayList<HelpPage>());

    }

    for (Command c : commands) {

      ArrayList<HelpPage> pages = sections.get(c.category);
      HelpPage page = null;

      if (pages.size() < 1) {
        page = new HelpPage();
        pages.add(page);
      } else {
        page = pages.get(pages.size() - 1);
      }

      if (page.commandsOnPage.size() == commandsPerPage) {
        page = new HelpPage();
        pages.add(page);
      }

      page.commandsOnPage.add(c);

    }

  }

  public static MessageCreateData getBasePage() {

    EmbedBuilder embed = baseEmbed();
    embed.setTitle("VadBot Commands Overview");
    embed.setDescription(
        "To see the different functionalities of VadBot, start by selecting a "
            + "category of commands in the dropdown below. \n\n **To see the " +
            "website version of help instead, use /website.**"
    );
    MessageCreateBuilder message = new MessageCreateBuilder()
        .setEmbeds(embed.build())
        .addActionRow(getMenu());

    return message.build();

  }

  public static MessageCreateData getFirstPage(Category cat) {

    EmbedBuilder embed = baseEmbed();
    embed.setTitle(formatCategory(cat) + " Commands");
    embed.setDescription(sections.get(cat).get(0).toString());
    embed.setFooter("Page 1");

    MessageCreateBuilder message = new MessageCreateBuilder()
        .setEmbeds(embed.build())
        .addActionRow(getMenu());

    if (sections.get(cat).size() > 1)
      message.addActionRow(Button.primary("help-nextpage", "Next"));

    return message.build();

  }

  public static MessageCreateData getNextPage(int currentPage, Category cat) {

    EmbedBuilder embed = baseEmbed();
    embed.setTitle(formatCategory(cat) + " Commands");
    embed.setDescription(sections.get(cat).get(currentPage).toString());
    embed.setFooter("Page " + (currentPage + 1));
    MessageCreateBuilder message = new MessageCreateBuilder()
        .setEmbeds(embed.build())
        .addActionRow(getMenu());

    if (currentPage < sections.get(cat).size() - 1)
      message.addActionRow(
          Button.primary("help-prevpage", "Back"),
          Button.primary("help-nextpage", "Next")
      );
    else
      message.addActionRow(Button.primary("help-prevpage", "Back"));

    return message.build();

  }

  public static MessageCreateData
      getPreviousPage(int currentPage, Category cat) {

    EmbedBuilder embed = baseEmbed();
    embed.setTitle(formatCategory(cat) + " Commands");
    embed.setDescription(sections.get(cat).get(currentPage - 2).toString());
    embed.setFooter("Page " + (currentPage - 1));
    MessageCreateBuilder message = new MessageCreateBuilder()
        .setEmbeds(embed.build())
        .addActionRow(getMenu());

    if (currentPage > 2)
      message.addActionRow(
          Button.primary("help-prevpage", "Back"),
          Button.primary("help-nextpage", "Next")
      );
    else
      message.addActionRow(Button.primary("help-nextpage", "Next"));

    return message.build();

  }

  private static EmbedBuilder baseEmbed() {

    return (new EmbedBuilder()).setColor(ConstantIDs.color);

  }

  private static StringSelectMenu getMenu() {

    StringSelectMenu menu = StringSelectMenu.create("help-choices")
        .setPlaceholder("Choose a category...")
        .addOption("Fun", "help-fun", "Commands that are just for fun.")
        .addOption(
            "Utility",
            "help-utility",
            "Commands that are useful in some way."
        )
        .addOption(
            "Moderator",
            "help-moderator",
            "Restricted commands for admins for moderation purposes."
        )
        .addOption(
            "Music",
            "help-music",
            "Commands related to VadBot's music player capabilities."
        )
        .addOption(
            "Leaderboard",
            "help-leaderboard",
            "Commands related to ranks in the server."
        )
        .addOption(
            "Miscellaneous",
            "help-misc",
            "Commands that don't fit any other category."
        )
        .build();

    return menu;

  }

  private static String formatCategory(Category cat) {

    return cat.toString().substring(0, 1)
        + cat.toString().substring(1).toLowerCase();

  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    event.getHook().sendMessage(getBasePage()).queue();

  }

}
