package events;

import commands.VoiceChannelManager.MusicManager;
import commands.VoiceChannelManager.VadBotDJ;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import vadbot.ConstantIDs;

public class VoiceUpdateHandler {

  public static void execute(GuildVoiceUpdateEvent event) {

    if (event.getMember().getId().equals(ConstantIDs.vadbot)) {

      if (event.getChannelJoined() == null && event.getChannelLeft() != null) {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        mm.queueManager.loopOn = false;
        mm.queueManager.audioPlayer.stopTrack();
        mm.queueManager.queue.clear();

        AudioManager am = event.getGuild().getAudioManager();
        am.closeAudioConnection();

      }

    } else {

      if (
        event.getChannelLeft() != null
            && event.getChannelLeft().getMembers().size() < 2
      ) {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        mm.queueManager.loopOn = false;
        mm.queueManager.audioPlayer.stopTrack();
        mm.queueManager.queue.clear();

        AudioManager am = event.getGuild().getAudioManager();
        am.closeAudioConnection();

      }

    }

  }

}
