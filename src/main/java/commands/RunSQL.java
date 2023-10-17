package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import vadbot.Database;

public class RunSQL implements AbstractVadBotCommand {

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    
    String sql = event.getOption("query").getAsString();
    boolean success = Database.executeSQL(sql);
    
    if (success)
      event.getHook().sendMessage("The SQL was executed successfully.").queue();
    else
      event.getHook().sendMessage("An error occurred while executing the SQL.").queue();

  }

}
