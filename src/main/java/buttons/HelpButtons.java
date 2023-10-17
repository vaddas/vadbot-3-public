package buttons;

import commands.HelpList;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import vadbot.Registry.Command.Category;

public class HelpButtons implements AbstractVadBotButton {

  @Override
  public void execute(ButtonInteractionEvent event) {

    int currentPage = Integer.valueOf(
        event.getMessage().getEmbeds().get(0).getFooter().getText().substring(5)
    );

    Category cat = Category.valueOf(
        event.getMessage().getEmbeds().get(0).getTitle().split(" ")[0]
            .toUpperCase()
    );

    switch (event.getButton().getId()) {

    case "help-nextpage":
      event
          .editMessage(
              MessageEditData
                  .fromCreateData(HelpList.getNextPage(currentPage, cat))
          )
          .queue();
      break;
    case "help-prevpage":
      event
          .editMessage(
              MessageEditData
                  .fromCreateData(HelpList.getPreviousPage(currentPage, cat))
          )
          .queue();

    }

  }

}
