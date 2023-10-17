package commands;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ChoiceMaker implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    String options = event.getOption("options").getAsString().trim();
    List<String> optionList = Arrays.asList(options.split(","));

    boolean wtf = false;

    // Deals with most situations with goofy whitespace and empty string issues
    // that cause exceptions
    try {
      optionList.removeAll(Arrays.asList(""));
    } catch (UnsupportedOperationException uoe) {
      wtf = true;
    }

    // Deals with option lists with no commas, or a single item starting or
    // ending with a comma
    if (
      wtf || optionList.size() < 2
          || (optionList.size() == 2
              && (options.startsWith(",") || options.endsWith(",")))
    ) {

      event.getHook().sendMessage(
          "Please enter a valid list of items, separated by commas. There's no sense in choosing from a list of one item."
      ).queue();

    } else {

      // If whitespace characters still exist in the option lists, this while
      // loop finally stomps them out by just rerunning the randomization until
      // a non whitespace option is found.
      String choice = null;
      while (choice == null || choice.isBlank())
        choice = optionList.get(new Random().nextInt(optionList.size()));

      event.getHook().sendMessage("I choose **" + choice.trim() + "**.").queue();

    }

  }

}
