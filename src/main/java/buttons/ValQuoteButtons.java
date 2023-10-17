package buttons;

import commands.ValQuote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class ValQuoteButtons implements AbstractVadBotButton {

  @Override
  public void execute(ButtonInteractionEvent event) {

    String character = event.getMessage()
        .getEmbeds()
        .get(0)
        .getTitle()
        .toLowerCase();
    String currentQuote = event.getMessage()
        .getEmbeds()
        .get(0)
        .getDescription();

    switch (event.getButton().getId()) {

    case "valquote-newquote":
      MessageEmbed newQuoteEmbed = null;
      int i = 0;
      while (
        i < 6 && (newQuoteEmbed == null
            || newQuoteEmbed.getDescription().equals(currentQuote))
      ) {
        newQuoteEmbed = ValQuote.makeEmbed(character, event.getJDA()).build();
        i++;
      }
      event.editMessageEmbeds(newQuoteEmbed).queue();
      break;

    case "valquote-newcharacter":
      MessageEmbed newCharacterEmbed = null;
      while (
        newCharacterEmbed == null
            || newCharacterEmbed.getTitle().toLowerCase().equals(character)
      )
        newCharacterEmbed = ValQuote.makeEmbed("", event.getJDA()).build();
      event.editMessageEmbeds(newCharacterEmbed).queue();
      break;

    case "valquote-list":
      MessageEmbed listEmbed = ValQuote.allQuotes(character, event.getJDA())
          .build();
      MessageEditData listMessage = new MessageEditBuilder()
          .setEmbeds(listEmbed)
          .setActionRow(Button.primary("valquote-one", "Back to One Quote"))
          .build();
      event.editMessage(listMessage).queue();
      break;

    case "valquote-one":
      EmbedBuilder embed = ValQuote.makeEmbed(character, event.getJDA());
      MessageEditData oneMessage = new MessageEditBuilder()
          .setEmbeds(embed.build())
          .setActionRow(
              Button.primary("valquote-list", "List"),
              Button.primary("valquote-newquote", "Different Quote"),
              Button.primary("valquote-newcharacter", "Different Character")
          )
          .build();
      event.editMessage(oneMessage).queue();

    }

  }

}
