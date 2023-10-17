package commands;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import vadbot.ConstantIDs;

public class VoiceChannelManager implements AbstractVadBotCommand {

  private static boolean currentlyPaused = false;

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    MessageCreateData message = null;

    switch (event.getName()) {

    case "join":
      message = joinChannel(event);
      break;
    case "leave":
      message = leaveChannel(event);
      break;
    case "play":
      playStuff(event);
      break;
    case "clearqueue":
      message = clearStuff(event);
      break;
    case "pause":
      message = pause(event);
      break;
    case "resume":
      message = resume(event);
      break;
    case "skip":
      message = skipStuff(event);
      break;
    case "nowplaying":
      message = nowPlaying(event);
      break;
    case "queue":
      message = getQueue(event);
      break;
    case "shuffle":
      message = shuffleQueue(event);
      break;
    case "loop":
      message = toggleLoop(event);
      break;

    }

    if (message != null)
      event.getHook().sendMessage(message).queue();

  }

  public static class QueueManager extends AudioEventAdapter {

    public final AudioPlayer audioPlayer;
    public BlockingQueue<AudioTrack> queue;
    public boolean loopOn = false;

    public QueueManager(AudioPlayer audioPlayer) {

      this.audioPlayer = audioPlayer;
      queue = new LinkedBlockingQueue<>();

    }

    public void queue(AudioTrack track) {

      if (!audioPlayer.startTrack(track, true))
        queue.offer(track);

    }

    public void nextTrack() {

      audioPlayer.startTrack(queue.poll(), false);

    }

    @Override
    public void onTrackEnd(
        AudioPlayer player,
        AudioTrack track,
        AudioTrackEndReason endReason) {

      if (endReason.mayStartNext) {
        if (loopOn)
          player.startTrack(track.makeClone(), false);
        else
          nextTrack();

      }

    }

  }

  public static class AudioStream implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;

    public AudioStream(AudioPlayer ap) {

      audioPlayer = ap;
      buffer = ByteBuffer.allocate(1024);
      frame = new MutableAudioFrame();
      frame.setBuffer(buffer);

    }

    @Override
    public boolean canProvide() {

      return audioPlayer.provide(frame);

    }

    @Override
    public ByteBuffer provide20MsAudio() {

      final Buffer buffer = ((Buffer) this.buffer).flip();
      return (ByteBuffer) buffer;

    }

    public boolean isOpus() {
      return true;
    }

  }

  public static class MusicManager {

    public final AudioPlayer audioPlayer;
    public final QueueManager queueManager;
    private final AudioStream audioStream;

    public MusicManager(AudioPlayerManager manager) {

      audioPlayer = manager.createPlayer();
      queueManager = new QueueManager(audioPlayer);
      audioPlayer.addListener(queueManager);
      audioStream = new AudioStream(audioPlayer);

    }

    public AudioStream getAudioStream() {

      return audioStream;

    }

  }

  public static class VadBotDJ {

    private static VadBotDJ instance;
    private final Map<Long, MusicManager> musicManagers;
    private final AudioPlayerManager apm;

    public VadBotDJ() {

      musicManagers = new HashMap<>();
      apm = new DefaultAudioPlayerManager();

      AudioSourceManagers.registerRemoteSources(apm);
      AudioSourceManagers.registerLocalSource(apm);

    }

    public MusicManager getMusicManager(Guild g) {

      return musicManagers.computeIfAbsent(g.getIdLong(), (guildId) -> {

        final MusicManager mm = new MusicManager(apm);
        g.getAudioManager().setSendingHandler(mm.getAudioStream());
        return mm;

      });

    }

    public static VadBotDJ getInstance() {

      if (instance == null)
        instance = new VadBotDJ();

      return instance;

    }

    public void loadAndPlay(
        SlashCommandInteractionEvent event,
        String url,
        boolean linkWasUrl) {

      final MusicManager mm = getMusicManager(event.getGuild());

      apm.loadItemOrdered(mm, url, new AudioLoadResultHandler() {

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
          mm.queueManager.queue(audioTrack);

          event.getHook()
              .sendMessage(
                  "Adding to queue: **" + audioTrack.getInfo().title
                      + "** by **" + audioTrack.getInfo().author + "**"
                      + (currentlyPaused
                          ? "\nNote: Bot is currently paused."
                          : "")
              )
              .queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {

          if (!linkWasUrl) {
            trackLoaded(playlist.getTracks().get(0));
          } else {

            final List<AudioTrack> tracks = playlist.getTracks();
            if (!tracks.isEmpty()) {

              for (AudioTrack at : tracks)
                mm.queueManager.queue(at);

              event.getHook()
                  .sendMessage(
                      "Adding all songs to queue from **" + playlist.getName()
                          + "** which is " + tracks.size() + " tracks in total."
                          + (currentlyPaused
                              ? "\nNote: Bot is currently paused."
                              : "")
                  )
                  .queue();

            }

          }

        }

        @Override
        public void noMatches() {

          event.getHook()
              .sendMessage("No tracks were found for that query.")
              .queue();

        }

        @Override
        public void loadFailed(FriendlyException exception) {
          throw new IllegalArgumentException("Jeeff");
        }

      });

    }

  }

  public MessageCreateData joinChannel(SlashCommandInteractionEvent event) {

    return joinChannel(event.getMember().getVoiceState().getChannel());

  }

  public MessageCreateData joinChannel(AudioChannelUnion channel) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    if (channel != null) {

      AudioManager audioManager = channel.getGuild().getAudioManager();
      audioManager.openAudioConnection(channel);
      builder.setContent("Ok");

    } else {

      builder.setContent("Get in a VC first silly");

    }

    return builder.build();

  }

  public MessageCreateData leaveChannel(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();
    Member self = event.getGuild().getSelfMember();
    GuildVoiceState selfState = self.getVoiceState();

    if (!selfState.inAudioChannel()) {

      builder.setContent("I'm not even in a VC silly");

    } else {

      Member member = event.getMember();
      GuildVoiceState memberState = member.getVoiceState();

      if (!memberState.inAudioChannel()) {

        builder.setContent("Get in VC first silly");

      } else {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        mm.queueManager.loopOn = false;
        mm.queueManager.audioPlayer.stopTrack();
        mm.queueManager.queue.clear();

        AudioManager am = event.getGuild().getAudioManager();
        am.closeAudioConnection();
        builder.setContent("Ok bye");

      }

    }

    return builder.build();

  }

  public void playStuff(SlashCommandInteractionEvent event) {

    if (!event.getMember().getVoiceState().inAudioChannel())
      event.getHook().sendMessage("Get in a VC first silly").queue();
    else {

      if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel memberChannel = event.getMember()
            .getVoiceState()
            .getChannel()
            .asVoiceChannel();

        audioManager.openAudioConnection(memberChannel);

      }

      String link = event.getOption("query").getAsString();

      boolean linkIsUrl = true;
      if (!isUrl(link)) {
        link = "ytsearch:" + link;
        linkIsUrl = false;
      }

      VadBotDJ.getInstance().loadAndPlay(event, link, linkIsUrl);

    }

  }

  public MessageCreateData clearStuff(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    Member self = event.getGuild().getSelfMember();
    GuildVoiceState selfState = self.getVoiceState();

    if (!selfState.inAudioChannel()) {

      builder.setContent("I'm not even in a VC silly");

    } else {

      Member member = event.getMember();
      GuildVoiceState memberState = member.getVoiceState();

      if (!memberState.inAudioChannel()) {

        builder.setContent("Get in VC first silly");

      } else {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        mm.queueManager.audioPlayer.stopTrack();
        mm.queueManager.queue.clear();

        builder.setContent("The track was stopped and the queue was cleared.");

      }

    }

    return builder.build();

  }

  public MessageCreateData pause(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    Member self = event.getGuild().getSelfMember();
    GuildVoiceState selfState = self.getVoiceState();

    if (!selfState.inAudioChannel()) {

      builder.setContent("I'm not even in a VC silly");

    } else {

      Member member = event.getMember();
      GuildVoiceState memberState = member.getVoiceState();

      if (!memberState.inAudioChannel()) {

        builder.setContent("Get in VC first silly");

      } else {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        if (!mm.queueManager.audioPlayer.isPaused()) {
          mm.queueManager.audioPlayer.setPaused(true);
          builder.setContent("Paused the music.");
          currentlyPaused = true;
        } else {
          builder.setContent(
              "The music is already paused. Use /resume to resume it."
          );
        }

      }

    }

    return builder.build();

  }

  public MessageCreateData resume(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    Member self = event.getGuild().getSelfMember();
    GuildVoiceState selfState = self.getVoiceState();

    if (!selfState.inAudioChannel()) {

      builder.setContent("I'm not even in a VC silly");

    } else {

      Member member = event.getMember();
      GuildVoiceState memberState = member.getVoiceState();

      if (!memberState.inAudioChannel()) {

        builder.setContent("Get in VC first silly");

      } else {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        if (mm.queueManager.audioPlayer.isPaused()) {
          mm.queueManager.audioPlayer.setPaused(false);
          currentlyPaused = false;
          builder.setContent("Resumed the music.");
        } else {
          builder.setContent(
              "The music is not paused currently. Use /pause to pause it."
          );
        }

      }

    }

    return builder.build();

  }

  public MessageCreateData toggleLoop(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    Member self = event.getGuild().getSelfMember();
    GuildVoiceState selfState = self.getVoiceState();

    if (!selfState.inAudioChannel()) {

      builder.setContent("I'm not even in a VC silly");

    } else {

      Member member = event.getMember();
      GuildVoiceState memberState = member.getVoiceState();

      if (!memberState.inAudioChannel()) {

        builder.setContent("Get in VC first silly");

      } else {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        mm.queueManager.loopOn = !mm.queueManager.loopOn;

        builder.setContent(
            "Loop is now " + (mm.queueManager.loopOn ? "**ON**." : "**OFF**.")
                + (currentlyPaused ? "\nNote: Bot is currently paused." : "")
        );

      }

    }

    return builder.build();

  }

  public MessageCreateData shuffleQueue(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    Member self = event.getGuild().getSelfMember();
    GuildVoiceState selfState = self.getVoiceState();

    if (!selfState.inAudioChannel()) {

      builder.setContent("I'm not even in a VC silly");

    } else {

      Member member = event.getMember();
      GuildVoiceState memberState = member.getVoiceState();

      if (!memberState.inAudioChannel()) {

        builder.setContent("Get in VC first silly");

      } else {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        BlockingQueue<AudioTrack> queue = mm.queueManager.queue;

        if (queue.isEmpty()) {

          builder.setContent("There is no queue to shuffle.");

        } else if (queue.size() == 1) {

          builder.setContent("There is only one song in the queue.");

        } else {

          List<AudioTrack> list = new ArrayList<AudioTrack>(queue);
          Collections.shuffle(list);
          mm.queueManager.queue = new LinkedBlockingQueue<AudioTrack>(list);

          builder.setContent(
              "Queue has been shuffled."
                  + (currentlyPaused ? "\nNote: Bot is currently paused." : "")
          );

        }

      }

    }

    return builder.build();

  }

  public MessageCreateData skipStuff(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    Member self = event.getGuild().getSelfMember();
    GuildVoiceState selfState = self.getVoiceState();

    if (!selfState.inAudioChannel()) {

      builder.setContent("I'm not even in a VC silly");

    } else {

      Member member = event.getMember();
      GuildVoiceState memberState = member.getVoiceState();

      if (!memberState.inAudioChannel()) {

        builder.setContent("Get in VC first silly");

      } else {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        AudioPlayer ap = mm.audioPlayer;

        if (ap.getPlayingTrack() != null) {

          mm.queueManager.nextTrack();
          builder.setContent(
              "Current track was skipped."
                  + (currentlyPaused ? "\nNote: Bot is currently paused." : "")
          );

        } else {

          builder.setContent("No tracks are currently playing.");

        }

      }

    }

    return builder.build();

  }

  public MessageCreateData nowPlaying(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    Member self = event.getGuild().getSelfMember();
    GuildVoiceState selfState = self.getVoiceState();

    if (!selfState.inAudioChannel()) {

      builder.setContent("I'm not even in a VC silly");

    } else {

      Member member = event.getMember();
      GuildVoiceState memberState = member.getVoiceState();

      if (!memberState.inAudioChannel()) {

        builder.setContent("Get in VC first silly");

      } else {

        MusicManager mm = VadBotDJ.getInstance()
            .getMusicManager(event.getGuild());

        AudioPlayer ap = mm.audioPlayer;
        AudioTrack currentTrack = ap.getPlayingTrack();

        if (currentTrack != null) {

          AudioTrackInfo info = currentTrack.getInfo();
          Duration dur = Duration.ofMillis(info.length);
          long hours = dur.toHours();
          long mins = dur.minusHours(hours).toMinutes();
          long seconds = dur.minusHours(hours).minusMinutes(mins).toSeconds();
          String duration = (hours > 0 ? hours + "h " : "")
              + (mins > 0 ? mins + "m " : "") + seconds + "s";

          EmbedBuilder embed = new EmbedBuilder()
              .setTitle("Now Playing in " + event.getGuild().getName())
              .setColor(ConstantIDs.color)
              .setDescription(
                  (currentlyPaused ? "**PAUSED**\n " : "") + "Title: **"
                      + info.title + "**\nPosted by: **" + info.author
                      + "**\n Track Length: **" + duration + "**\nLink: "
                      + info.uri
              );

          builder.setEmbeds(embed.build());

        } else {

          builder.setContent("There is no track currently playing.");

        }

      }

    }

    return builder.build();

  }

  public MessageCreateData getQueue(SlashCommandInteractionEvent event) {

    MessageCreateBuilder builder = new MessageCreateBuilder();

    MusicManager mm = VadBotDJ.getInstance().getMusicManager(event.getGuild());

    BlockingQueue<AudioTrack> queue = mm.queueManager.queue;

    if (queue.isEmpty()) {

      builder.setContent("The queue is currently empty.");

    } else {

      int trackCount = Math.min(queue.size(), 10);
      List<AudioTrack> trackList = new ArrayList<>(queue);

      String description = "Now Playing: **"
          + mm.audioPlayer.getPlayingTrack().getInfo().title + "** by **"
          + mm.audioPlayer.getPlayingTrack().getInfo().author + "** \n\n";
      for (int i = 0; i < trackCount; i++) {

        description += "**" + (i + 1) + ". " + trackList.get(i).getInfo().title
            + "** by **" + trackList.get(i).getInfo().author + "**\n\n";

      }

      int remainingTracks = queue.size() - trackCount;
      if (remainingTracks > 0)
        description += remainingTracks + " more tracks not shown.";

      EmbedBuilder embed = new EmbedBuilder()
          .setTitle("Up Next in " + event.getGuild().getName())
          .setColor(ConstantIDs.color)
          .setDescription(description);

      builder.setEmbeds(embed.build());

    }

    return builder.build();

  }

  private boolean isUrl(String url) {

    try {

      new URI(url);
      return true;

    } catch (URISyntaxException e) {

      return false;

    }

  }

}
