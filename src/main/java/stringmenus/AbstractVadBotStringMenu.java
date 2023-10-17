package stringmenus;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface AbstractVadBotStringMenu {

  public void execute(StringSelectInteractionEvent event);
  
}
