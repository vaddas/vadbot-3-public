package stringmenus;

import commands.HelpList;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import vadbot.Registry.Command.Category;

public class HelpMenu implements AbstractVadBotStringMenu {

  @Override
  public void execute(StringSelectInteractionEvent event) {

    String choice = event.getSelectedOptions().get(0).getLabel().toUpperCase();
    Category cat = Category.valueOf(choice);

    event
        .editMessage(MessageEditData.fromCreateData(HelpList.getFirstPage(cat)))
        .queue();

  }

}
