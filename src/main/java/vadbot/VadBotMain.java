package vadbot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import commands.BirthdayManager;
import commands.LastFmHandler;
import commands.SpotifyHandler;
import commands.ValQuote;
import de.umass.lastfm.Caller;
import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class VadBotMain extends ListenerAdapter {

  public static JDA jda;

  private static String token;
  private static String testToken;

  // TOGGLE TEST BOT OR MAIN BOT
  public static final boolean isTestBot = false;
  public static final boolean isLocal = false;

  public static void main(String[] args) {

    System.setOut(new VadBotPrintStream(System.out));
    System.setErr(new VadBotPrintStream(System.err));

    ValQuote.init();

    Properties props = new Properties();
    try {
      InputStream propsInput = VadBotMain.class.getClassLoader()
          .getResourceAsStream("config.properties");
      props.load(propsInput);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    token = props.getProperty("MAIN_TOKEN");
    testToken = props.getProperty("TEST_TOKEN");

    Database.setLogin(props.getProperty("DB_LOGIN"));
    SpotifyHandler.login(
        props.getProperty("SPOTIFY_CLIENT_ID"),
        props.getProperty("SPOTIFY_CLIENT_SECRET"),
        (isLocal
            ? props.getProperty("SPOTIFY_CLIENT_REDIRECT_LOCAL")
            : props.getProperty("SPOTIFY_CLIENT_REDIRECT"))
    );
    LastFmHandler.init(
        props.getProperty("LAST_FM_KEY"),
        props.getProperty("LAST_FM_SECRET"),
        props.getProperty("LAST_FM_CALLBACK_URL"),
        props.getProperty("LAST_FM_CALLBACK_URL_LOCAL")
    );

    VadBeacon.init(Integer.parseInt(props.getProperty("LOCAL_PORT")), 
        Integer.parseInt(props.getProperty("REMOTE_PORT")));

    JDA jda = JDABuilder.createDefault(isTestBot ? testToken : token)
        .enableIntents(
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.GUILD_MEMBERS
        )
        .enableCache(CacheFlag.VOICE_STATE)
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .setChunkingFilter(ChunkingFilter.ALL)
        .addEventListeners(new VadBotMain())
        .addEventListeners(new VadBotCommandListener())
        .build();

    try {

      VadBotMain.jda = jda.awaitReady();

    } catch (Exception e) {

      e.printStackTrace();

    }

    System.out.println("JDA is ready.");
    switchStatusOnInterval(3, jda);
    announceBirthdays(jda);
    Registry.registerCommands(jda);
    RankManager.initCooldowns();

    if (!isTestBot)
      MemberManager.init(jda);

    Caller.getInstance().setUserAgent("tst");

  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {

    MessageHandler.execute(event);

  }

  @Override
  public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

    MemberLeaveHandler.execute(event);

  }

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {

    MemberJoinHandler.execute(event);

  }

  @Override
  public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {

    VoiceUpdateHandler.execute(event);

  }

  @Override
  public void
      onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {

    NicknameUpdateHandler.execute(event);

  }

  @Override
  public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {

    RoleUpdateHandler.roleAdd(event);

  }

  @Override
  public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {

    RoleUpdateHandler.roleRemove(event);

  }

  public static void setStatus(JDA jda, String status) {

  }

  public static void shuffleStatus(JDA jda) {

    ArrayList<String> artists = new ArrayList<>(
        Arrays.asList(
            "Lana Del Rey",
            "Taylor Swift",
            "Joji",
            "The Neighbourhood",
            "Harry Styles",
            "Paramore",
            "The Weeknd",
            "One Direction",
            "Dua Lipa",
            "Lorde",
            "Bruno Mars",
            "Billie Eilish",
            "Ariana Grande",
            "Halsey",
            "C418",
            "Arctic Monkeys",
            "5 Seconds of Summer",
            "The Strokes",
            "Charli XCX",
            "Clairo",
            "Morgan Wallen",
            "COIN",
            "Olivia Rodrigo",
            "Louis Tomlinson",
            "Lena Raine",
            "Niall Horan",
            "Bad Suns",
            "Sabrina Carpenter",
            "Tame Impala",
            "Avril Lavigne",
            "Saint Motel",
            "Phoebe Bridgers",
            "genjilover001",
            "MisterWives",
            "Ethel Cain",
            "beabadoobee",
            "Radiohead",
            "Bazzi",
            "The Motto",
            "Glass Animals",
            "PinkPantheress",
            "TV Girl",
            "Cigarettes After Sex",
            "Deftones",
            "Matt Maltese",
            "Mac DeMarco",
            "Gorillaz",
            "Labrinth",
            "Joshua Bassett"
        )
    );

    String listen = artists.get((int) (Math.random() * artists.size()));

    jda.getPresence().setActivity(Activity.listening(listen));

  }

  public static void switchStatusOnInterval(int minutes, JDA jda) {

    ScheduledExecutorService executor = Executors
        .newSingleThreadScheduledExecutor();

    executor.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        shuffleStatus(jda);
      }
    }, 0, minutes, TimeUnit.MINUTES);

  }

  public static void announceBirthdays(JDA jda) {

    LocalTime now = LocalTime.now(ZoneId.of("America/New_York"));
    
    System.out.println(now);

    long initialDelay = LocalTime.of(0, 0).toSecondOfDay() - now.toSecondOfDay()
        + 10;
    if (initialDelay < 0) {
      initialDelay += TimeUnit.DAYS.toSeconds(1);
    }

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        (new BirthdayManager()).sendBirthdayMessage(jda);
        System.out.println("Executed birthday script.");
      }
    }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

  }

}
