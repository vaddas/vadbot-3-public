package buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface AbstractVadBotButton {

  public void execute(ButtonInteractionEvent event);
  
}
