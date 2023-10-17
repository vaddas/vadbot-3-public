package events;

import commands.VoiceChannelManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import vadbot.ConstantIDs;
import vadbot.RankManager;

public class MessageHandler {

  public static void execute(MessageReceivedEvent event) {

    String message = event.getMessage().getContentRaw().toLowerCase();
    User author = event.getAuthor();

    if (!author.isBot()) {
      RankManager.addXPtoMember(event.getMember(), event.getChannel());
    }

    if (!author.isBot() && event.getChannel().canTalk()) {

      if (
        message.contains(ConstantIDs.walkingchicken)
            || message.contains(ConstantIDs.paintlegend)
            || message.contains("🍗")
      )
        event.getMessage()
            .addReaction(Emoji.fromFormatted(ConstantIDs.walkingchicken))
            .queue();

      if (
        message.contains("vadbot") || message.contains("vaddy")
            || event.getMessage()
                .getMentions()
                .getUsers()
                .contains(
                    event.getGuild().getMemberById(ConstantIDs.vadbot).getUser()
                )
            || event.getMessage()
                .getMentions()
                .getRoles()
                .contains(event.getGuild().getRoleById(ConstantIDs.vadbotRole))
      ) {

        if (message.equals("right vadbot"))
          event.getChannel().sendMessage("right").queue();
        else
          event.getChannel().sendMessage("What").queue();

      } else if (
        message.startsWith("not you") || message.startsWith("shut up")
      ) {

        event.getChannel().sendMessage("sorry").queue();

      } else if (message.startsWith("i hate you")) {

        event.getChannel().sendMessage("☹️").queue();

      } else if (message.startsWith("!rank")) {

        event.getChannel().sendMessage(ConstantIDs.madalie).queue();

      }

      switch (message) {

      case "hi":
      case "hello":
      case "hola":
        event.getChannel().sendMessage("hi").queue();
        break;

      case "kys im scared":
        if (author.getId().equals(ConstantIDs.calvin)) {

          event.getMessage().addReaction(Emoji.fromUnicode("👋")).queue();
          event.getJDA().shutdown();

        }
        break;

      case "gg":
        event.getChannel().sendMessage("ez").queue();
        break;

      case "^":
        event.getChannel().sendMessage("^").queue();
        break;

      case "pensive":
        event.getChannel().sendMessage("😔").queue();
        break;

      case "vabdot":
        event.getChannel().sendMessage(ConstantIDs.walkingchicken).queue();
        break;

      case "all bots join vc":
        event.getMessage()
            .addReaction(Emoji.fromFormatted(ConstantIDs.letsgo))
            .queue();
        new VoiceChannelManager()
            .joinChannel(event.getMember().getVoiceState().getChannel());
        break;

      case "all bots leave vc":
        event.getMessage()
            .addReaction(Emoji.fromFormatted(ConstantIDs.dissolve))
            .queue();
        event.getGuild().getAudioManager().closeAudioConnection();
        break;

      }

    }

  }

}
