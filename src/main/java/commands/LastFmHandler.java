package commands;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.ResponseBuilder;
import de.umass.lastfm.Result;
import de.umass.lastfm.Session;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import vadbot.ConstantIDs;
import vadbot.Database;
import vadbot.VadBeacon;

public class LastFmHandler implements AbstractVadBotCommand {

  private static String lastfmKey;
  private static String lastfmSecret;
  private static String lastfmCallbackUrl;
  private static String lastfmCallbackUrlLocal;

  static class FmCallback implements VadBeacon.AuthCodeCallback {

    @Override
    public void onAuthCodeReceived(String authCode, String state) {

      System.out.println("Received FM authorization code for " + state);

      Session sesh = Authenticator
          .getSession(authCode, lastfmKey, lastfmSecret);

      Database
          .set("member_info", "fm_session", "member_id", state, sesh.getKey());

      System.out.println("Session saved.");

    }

  }

  static class TrackRecord {

    public LocalDate timePlayed;
    public String title;
    public String artist;

    public TrackRecord() {

    }

    public TrackRecord(Date date, String name, String singer) {

      timePlayed = date.toInstant()
          .atZone(ZoneId.of("America/New_York"))
          .toLocalDate();
      title = name;
      artist = singer;
    }

  }

  static class CSVDataReturn {

    public ArrayList<TrackRecord> records;
    long from;
    long to;

  }

  public static void init(String key, String secret, String callback, String localCallback) {

    VadBeacon.setFmCallback(new FmCallback());

    lastfmKey = key;
    lastfmSecret = secret;
    lastfmCallbackUrl = callback;
    lastfmCallbackUrlLocal = localCallback;

  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    MessageCreateData message = null;

    // non-auth required
    switch (event.getName()) {

    case "login":
      message = getLogin(event);
      break;

    }

    if (message != null) {
      event.getHook().sendMessage(message).queue();
      return;
    }

    if (!checkAuth(event.getUser().getId())) {

      event.getHook()
          .sendMessage(
              "Your Last.fm account isn't linked to VadBot yet. Use `/fm auth` to link your account, then try again."
          )
          .queue();
      return;

    }

    Session sesh = getSession(event.getUser().getId());
    if (User.getInfo(sesh) == null) {

      event.getHook()
          .sendMessage(
              "There was an error in authentication. Use `/fm auth` to re-link your account, then try again."
          )
          .queue();
      return;

    }

    // auth required
    switch (event.getName()) {

    case "fm":
      message = getInfo(event, sesh);
      break;
    case "trends":
      switch (event.getSubcommandName()) {

      case "track":
        message = getTrendChart(event, sesh, true);
        break;
      case "artist":
        message = getTrendChart(event, sesh, false);

      }
      break;

    }

    if (message != null)
      event.getHook().sendMessage(message).queue();
    else
      event.getHook().sendMessage("There was an error.").queue();

  }

  public MessageCreateData getLogin(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    message.setContent(
        "http://www.last.fm/api/auth/?api_key=" + lastfmKey + "&cb="
            + lastfmCallbackUrlLocal + "?discord=" + event.getUser().getId()
    );

    return message.build();

  }

  public MessageCreateData
      getInfo(SlashCommandInteractionEvent event, Session sesh) {

    MessageCreateBuilder message = new MessageCreateBuilder();

//    Collection<Track> topTracks = User
//        .getTopTracks(User.getInfo(sesh).getName(), Period.OVERALL, lastfmKey);
//
//    EmbedBuilder embed = new EmbedBuilder();
//    embed.setTitle("Your top overall tracks");
//    String description = "";
//
//    int i = 0;
//    for (Track track : topTracks) {
//      description += "**" + track.getName() + "** by **" + track.getArtist()
//          + "**\n\n";
//      if (++i == 5) {
//        break;
//      }
//    }

//    embed.setDescription(description);

//    message.setEmbeds(embed.build());
    String currentDir = System.getProperty("user.dir");
    Path resourcePath = Paths
        .get(currentDir, "src", "main", "resources", "temp");

    message.setContent(resourcePath.toString());

    return message.build();

  }

  public MessageCreateData getTrendChart(
      SlashCommandInteractionEvent event,
      Session sesh,
      boolean isTrackBased) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    LocalDateTime from = LocalDateTime.now()
        .minusMonths(11)
        .withDayOfMonth(1)
        .with(LocalTime.MIDNIGHT);
    LocalDateTime to = LocalDateTime.now();

    long fromEpoch = from.toInstant(ZoneOffset.UTC).getEpochSecond();
    long toEpoch = to.toInstant(ZoneOffset.UTC).getEpochSecond();

    String user = User.getInfo(sesh).getName();

    PaginatedResult<Track> scrobbles = getScrobbles(
        user,
        fromEpoch,
        toEpoch,
        1
    );

    ArrayList<TrackRecord> scrobbleData = collectData(
        user,
        event.getUser().getId(),
        toEpoch
    );

    Track currentlyPlaying = null;

    for (Track t : scrobbles.getPageResults()) {

      if (t.getName() != null && t.getArtist() != null) {
        currentlyPlaying = t;
        break;
      }

    }

    final String name = currentlyPlaying.getName();
    final String artist = currentlyPlaying.getArtist();

    ArrayList<TrackRecord> filtered = new ArrayList<>();

    for (TrackRecord t : scrobbleData) {

      if (isTrackBased) {

        if (t.title.equals(name) && t.artist.equals(artist))
          filtered.add(t);

      } else {

        if (t.artist.equals(artist))
          filtered.add(t);

      }

    }

    Map<String, Integer> monthCounts = new LinkedHashMap<>();

    LocalDateTime currentDate = from;
    while (!currentDate.isAfter(to)) {
      monthCounts
          .put(currentDate.getMonthValue() + "-" + currentDate.getYear(), 0);
      currentDate = currentDate.plus(1, ChronoUnit.MONTHS);
    }

    for (TrackRecord d : filtered) {

      int year = d.timePlayed.getYear();
      int month = d.timePlayed.getMonthValue();
      String monthKey = month + "-" + year;
      monthCounts.put(monthKey, monthCounts.getOrDefault(monthKey, 0) + 1);

    }

    DefaultCategoryDataset data = new DefaultCategoryDataset();
    for (Map.Entry<String, Integer> entry : monthCounts.entrySet())
      data.addValue(entry.getValue(), "Tracks", monthTranslate(entry.getKey()));

    JFreeChart chart = ChartFactory.createBarChart(
        "Monthly Playcounts for " + ((isTrackBased) ? (name + " by ") : "")
            + artist,
        "Month",
        "Playcount",
        data,
        PlotOrientation.VERTICAL,
        false,
        false,
        false
    );

    formatChart(chart, ConstantIDs.color);

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(800, 600));

    String currentDir = System.getProperty("user.dir");
    Path resourcePath = Paths
        .get(currentDir, "src", "main", "resources", "temp");
    String fileName = "/" + event.getUser().getId() + "_1YR_chart.png";
    String savePath = resourcePath.toString() + fileName;

    File saved = saveChartAsImage(chartPanel, savePath, 800, 600);

    if (saved != null) {
      FileUpload file = FileUpload.fromData(saved);
      message.addFiles(file);
    } else {
      message.setContent("There was an error creating the image.");
    }

    return message.build();

  }

  private ArrayList<TrackRecord>
      collectData(String user, String id, long toEpoch) {

    String currentDir = System.getProperty("user.dir");
    Path resourcePath = Paths
        .get(currentDir, "src", "main", "resources", "music_data");
    String fileName = "/" + id + "_scrobbles.csv";
    String savePath = resourcePath.toString() + fileName;

    File datafile = new File(savePath);

    if (!datafile.exists()) {

      PaginatedResult<Track> newScrobbles = getScrobbles(user, 0, toEpoch, 1);

      ArrayList<Track> everything = new ArrayList<>();
      everything.addAll(newScrobbles.getPageResults());
      int numPages = newScrobbles.getTotalPages();

      for (int i = 2; i <= numPages; i++) {

        newScrobbles = getScrobbles(user, 0, toEpoch, i);
        everything.addAll(newScrobbles.getPageResults());
        System.out.println("Collected tracks from page " + i);

      }

      ArrayList<Track> filtered = new ArrayList<>();

      for (Track t : everything) {
        if (
          t != null && t.getName() != null && t.getArtist() != null
              && t.getPlayedWhen() != null
        ) {

          filtered.add(t);

        }

      }

      writeMusicDataCSV(savePath, filtered, 0, toEpoch);

    }

    CSVDataReturn cacheData = readMusicDataCSV(savePath);
    ArrayList<TrackRecord> cacheScrobbles = cacheData.records;

    PaginatedResult<Track> postScrobbles = null;
    ArrayList<Track> postScrobbleFullCache = new ArrayList<>();
    ArrayList<TrackRecord> postScrobbleList = new ArrayList<>();

    if (toEpoch - cacheData.to > 0) {
      System.out.println("Yeah lets go baby");
      postScrobbles = getScrobbles(user, cacheData.to, toEpoch, 1);

      int numPages = postScrobbles.getTotalPages();

      for (int i = 1; i <= numPages; i++) {

        postScrobbles = getScrobbles(user, cacheData.to, toEpoch, i);
        for (Track t : postScrobbles.getPageResults()) {
          if (
            t != null && t.getName() != null && t.getArtist() != null
                && t.getPlayedWhen() != null
          ) {
            postScrobbleList.add(
                new TrackRecord(t.getPlayedWhen(), t.getName(), t.getArtist())
            );
            postScrobbleFullCache.add(t);
          }
        }
        System.out.println("Collected new tracks from page " + i);

      }

    }

    ArrayList<TrackRecord> everything = new ArrayList<>();
    everything.addAll(postScrobbleList);
    everything.addAll(cacheScrobbles);

    Executors.newSingleThreadExecutor().submit(() -> {

      writeMusicDataCSV(savePath, postScrobbleFullCache, 0, toEpoch);
      System.out.println("Done writing new data.");

    });

    return everything;

  }

  private static File
      saveChartAsImage(ChartPanel chartPanel, String savePath, int w, int h) {

    File imageFile = new File(savePath);
    try {
      ChartUtilities.saveChartAsPNG(imageFile, chartPanel.getChart(), w, h);
      return imageFile;
    } catch (IOException io) {
      io.printStackTrace();
      return null;
    }

  }

  private Session getSession(String id) {

    String sessionKey = Database
        .getByID("member_info", "fm_session", "member_id", id);
    return Session.createSession(lastfmKey, lastfmSecret, sessionKey);

  }

  private boolean checkAuth(String id) {

    if (Database.isNull("member_info", "fm_session", "member_id", id))
      return false;
    else
      return true;

  }

  private PaginatedResult<Track>
      getScrobbles(String user, long from, long to, int page) {

    Map<String, String> params = new HashMap<String, String>();

    params.put("user", user);
    params.put("limit", String.valueOf(200));
    params.put("page", String.valueOf(page));
    params.put("from", "" + from);
    params.put("to", "" + to);

    Caller caller = Caller.getInstance();
    caller.getLogger().setLevel(Level.OFF);

    Result result = caller.call("user.getRecentTracks", lastfmKey, params);
    return ResponseBuilder.buildPaginatedResult(result, Track.class);

  }

  private PaginatedResult<Track> searchTrack(String query, String artist) {

    Map<String, String> params = new HashMap<String, String>();

    params.put("track", query);
    params.put("api_key", lastfmKey);
    params.put("artist", artist);

    Result result = Caller.getInstance()
        .call("track.search", lastfmKey, params);
    return ResponseBuilder.buildPaginatedResult(result, Track.class);

  }

  private static void formatChart(JFreeChart chart, Color color) {

    Font bigFont = new Font("Georgia", Font.PLAIN, 24);
    Font font = new Font("Georgia", Font.PLAIN, 16);
    Font smallFont = new Font("Georgia", Font.PLAIN, 12);

    Color fakeWhite = new Color(0xff, 0xf8, 0xe1);
    Color fakeBlack = new Color(0x33, 0x33, 0x33);

    CategoryPlot plot = chart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();

    plot.setOutlineVisible(false);
    plot.setBackgroundPaint(fakeWhite);
    plot.setRangeGridlinePaint(fakeBlack);

    renderer.setShadowVisible(false);
    renderer.setBarPainter(new StandardBarPainter());
    renderer.setSeriesPaint(0, color);
    renderer.setItemMargin(0.0);
    chart.setPadding(new RectangleInsets(20.0, 1.0, 1.0, 1.0));

    chart.setBackgroundPaint(fakeWhite);
    chart.getTitle().setFont(bigFont);

    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setTickLabelFont(smallFont);
    domainAxis.setLabelFont(font);

    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setTickLabelFont(smallFont);
    rangeAxis.setLabelFont(font);

    int tickUnits = 0;
    int max = (int) rangeAxis.getUpperBound();
    tickUnits = (max >= 10) ? ((int) (Math.ceil(max / 10.0 / 5.0) * 5.0)) : 1;

    rangeAxis.setTickUnit(new NumberTickUnit(tickUnits));

  }

  private String monthTranslate(String month) {

    LocalDate date = LocalDate
        .parse(month + "-01", DateTimeFormatter.ofPattern("M-yyyy-d"));
    return date.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));

  }

  private void writeMusicDataCSV(
      String savePath,
      ArrayList<Track> tracks,
      long from,
      long to) {

    try {

      ArrayList<Track> filtered = new ArrayList<>();

      for (Track t : tracks) {

        if (
          t.getPlayedWhen() != null && t.getName() != null
              && t.getArtist() != null
        )
          filtered.add(t);

      }

      File file = new File(savePath);

      boolean fileExists = file.exists();

      FileWriter fileWrite = new FileWriter(file);

      CSVPrinter csvPrinter = new CSVPrinter(fileWrite, CSVFormat.DEFAULT);

      if (fileExists) {
        System.out.println("Yeah baby the file exists");

        FileReader fileRead = new FileReader(file);

        CSVParser parser = CSVFormat.DEFAULT.parse(fileRead);

        List<CSVRecord> records = parser.getRecords();

        boolean isFirstLine = true;
        for (CSVRecord record : records) {
          if (isFirstLine) {
            csvPrinter.printRecord("Info", from, to);
            isFirstLine = false;
          } else {
            csvPrinter.printRecord(record);
          }
        }

        parser.close();
        csvPrinter.flush();

      } else {
        csvPrinter.printRecord("Info", from, to);
      }

      int i = 0;
      for (Track track : filtered) {
        String timePlayed = track.getPlayedWhen()
            .toInstant()
            .atZone(ZoneId.of("America/New_York"))
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("MM-dd-yy"));
        csvPrinter.printRecord(timePlayed, track.getName(), track.getArtist());

        if ((++i) % 50 == 0)
          System.out.println("Wrote " + i + " tracks to CSV.");

      }

      csvPrinter.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private CSVDataReturn readMusicDataCSV(String filename) {

    try {

      CSVDataReturn data = new CSVDataReturn();

      data.records = new ArrayList<>();

      FileReader file = new FileReader(filename);

      CSVParser parser = CSVFormat.DEFAULT.parse(file);

      List<CSVRecord> records = parser.getRecords();

      data.from = Long.parseLong(records.get(0).get(1));

      data.to = Long.parseLong(records.get(0).get(2));

      for (int i = 1; i < records.size(); i++) {

        CSVRecord csvRecord = records.get(i);
        TrackRecord record = new TrackRecord();
        record.timePlayed = LocalDate
            .parse(csvRecord.get(0), DateTimeFormatter.ofPattern("MM-dd-yy"));
        record.title = csvRecord.get(1);
        record.artist = csvRecord.get(2);

        data.records.add(record);

        if (i % 200 == 0)
          System.out.println("Read " + i + " tracks from CSV.");

      }

      return data;

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }

}
