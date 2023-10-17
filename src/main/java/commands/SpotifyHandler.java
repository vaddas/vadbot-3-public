package commands;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.hc.core5.http.ParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import vadbot.Database;
import vadbot.VadBeacon;

public class SpotifyHandler implements AbstractVadBotCommand {

  private static final String scope = "playlist-read-private playlist-modify-private playlist-modify-public user-top-read user-read-currently-playing user-library-read";

  private static SpotifyApi spotify;
  private static String clientID;
  private static String clientSecret;
  private static String redirectURL;

  private static HashMap<String, String> stateTranslator = new HashMap<>();

  static class SpotifyCallback implements VadBeacon.AuthCodeCallback {

    @Override
    public void onAuthCodeReceived(String authCode, String state) {

      System.out.println("Received authorization code for " + state);
      
      try {
        AuthorizationCodeCredentials creds = spotify.authorizationCode(authCode)
            .build()
            .execute();
        storeRefresh(stateTranslator.get(state), creds.getRefreshToken());

      } catch (ParseException | SpotifyWebApiException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

  }

  public static void login(String id, String secret, String redirect) {

    VadBeacon.setCallback(new SpotifyCallback());

    spotify = new SpotifyApi.Builder().setClientId(id)
        .setClientSecret(secret)
        .setRedirectUri(URI.create(redirect))
        .build();
    
    clientID = id;
    clientSecret = secret;
    redirectURL = redirect;

  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    MessageCreateData message = null;

    // non-auth requirement commands
    switch (event.getSubcommandName()) {

    case "auth":
      message = sendAuthLink(event);
      break;

    }

    if (message != null) {
      event.getHook().sendMessage(message).queue();
      return;
    }

    if (!checkAuth(event.getUser().getId())) {
      event.getHook()
          .sendMessage(
              "Your Spotify isn't linked to VadBot yet. Use `/spotify auth` to link your Spotify, then try again."
          )
          .queue();
      return;
    }

    // auth-required commands
    switch (event.getSubcommandName()) {

    case "nowplaying":
      message = test(event);
      break;
    case "recs":
      message = recs(event);
      break;

    }

    if (message != null)
      event.getHook().sendMessage(message).queue();

  }

  public MessageCreateData recs(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    try {

      boolean success = setAccessToken(event.getUser().getId());
      if (!success) {
        throw new SpotifyWebApiException(
            "Authentication error. You should try re-authenticating using `/spotify auth.`"
        );
      }

      Track track;
      if (event.getOption("track") == null) {
        CurrentlyPlaying nowPlaying = spotify.getUsersCurrentlyPlayingTrack()
            .build()
            .execute();
        
        if (nowPlaying != null) {
          track = spotify.getTrack(nowPlaying.getItem().getId())
              .build()
              .execute();
        } else {
          message.setContent("You're not listening to anything currently.");
          return message.build();
        }
        
      } else {
        track = spotify.searchTracks(event.getOption("track").getAsString()).build().execute().getItems()[0];
      }
      
      Recommendations recs = spotify.getRecommendations()
          .seed_tracks(track.getId())
          .limit(5)
          .build()
          .execute();
      ArrayList<TrackSimplified> recommended = new ArrayList<>(
          Arrays.asList(recs.getTracks())
      );

      String messageContent = "If you like **" + track.getName() + "** by **"
          + track.getArtists()[0].getName() + "**"+ ", then you might like these tracks: \n\n";
      for (TrackSimplified t : recommended) {
        messageContent += "**" + t.getName() + "** by **"
            + t.getArtists()[0].getName() + "**\n";
        messageContent += t.getExternalUrls().get("spotify") + "\n\n";
      }
      
      message.setContent(messageContent);

    } catch (ParseException | SpotifyWebApiException | IOException e) {
      message.setContent("There was an error: " + e.getMessage());
      e.printStackTrace();
    }

    return message.build();

  }

  public MessageCreateData nowPlaying(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    try {

      boolean success = setAccessToken(event.getUser().getId());
      if (!success) {
        throw new SpotifyWebApiException(
            "Authentication error. You should try re-authenticating using `/spotify auth.`"
        );
      }

      CurrentlyPlaying nowPlaying = spotify.getUsersCurrentlyPlayingTrack()
          .build()
          .execute();

      if (nowPlaying != null) {
        Track track = spotify.getTrack(nowPlaying.getItem().getId())
            .build()
            .execute();

        message.setContent(
            "You're listening to **" + track.getName() + "** by **"
                + track.getArtists()[0].getName() + "**"
        );
      } else {
        message.setContent("You're not listening to anything currently.");
      }

    } catch (ParseException | SpotifyWebApiException | IOException e) {
      message.setContent("There was an error: " + e.getMessage());
      e.printStackTrace();
    }

    return message.build();

  }

  public MessageCreateData sendAuthLink(SlashCommandInteractionEvent event) {

    MessageCreateBuilder message = new MessageCreateBuilder();

    AuthorizationCodeUriRequest request = spotify.authorizationCodeUri()
        .scope(scope)
        .state(generateUniqueState(event.getUser()))
        .build();

    URI url = request.execute();

    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("Spotify Authentication");
    embed.setDescription(
        "To give VadBot access to your Spotify data, please follow [this link]("
            + url.toString() + ")."
    );

    event.getUser().openPrivateChannel().queue((dm) -> {
      dm.sendMessageEmbeds(embed.build()).queue();
    });

    message.setContent(
        "Check your DMs for a message with an authentication link!"
    );

    return message.build();

  }

  public static String generateUniqueState(User user) {

    String state = null;

    if (user != null) {

      state = user.getName().trim().replaceAll("\\s", "0");

      if (state.length() > 16) {
        state = state.substring(0, 16);
      } else {
        int fill = 16 - state.length();
        for (int i = 0; i < fill; i++)
          state += "7";
      }

      stateTranslator.put(state, user.getId());

    }

    return state;

  }

  public static boolean setAccessToken(String id) {

    String token = Database
        .getByID("member_info", "sp_refresh_token", "member_id", id);

    spotify = new SpotifyApi.Builder().setClientId(clientID)
        .setClientSecret(clientSecret)
        .setRedirectUri(URI.create(redirectURL))
        .setRefreshToken(token)
        .build();

    try {

      AuthorizationCodeCredentials creds = spotify.authorizationCodeRefresh()
          .build()
          .execute();
      spotify.setAccessToken(creds.getAccessToken());
      storeRefresh(id, spotify.getRefreshToken());

    } catch (ParseException | SpotifyWebApiException | IOException e) {
      // TODO Auto-generated catch block
      System.err.println(e.getMessage());
      return false;
    }

    return true;

  }

  public static void storeRefresh(String id, String token) {

    Database.set("member_info", "sp_refresh_token", "member_id", id, token);
    System.out.println("New refresh token: " + token);

  }

  public static boolean checkAuth(String id) {

    if (Database.isNull("member_info", "sp_refresh_token", "member_id", id))
      return false;
    else
      return true;

  }
  
  public MessageCreateData test(SlashCommandInteractionEvent event) {
    
    MessageCreateBuilder message = new MessageCreateBuilder();

    try {

      boolean success = setAccessToken(event.getUser().getId());
      if (!success) {
        throw new SpotifyWebApiException(
            "Authentication error. You should try re-authenticating using `/spotify auth.`"
        );
      }

      Paging<Track> nowPlaying = spotify.getUsersTopTracks().build().execute();

      if (nowPlaying != null) {
        Track track = spotify.getTrack(nowPlaying.getItems()[0].getId())
            .build()
            .execute();

        message.setContent(
            "Your top track is **" + track.getName() + "** by **"
                + track.getArtists()[0].getName() + "**"
        );
      } else {
        message.setContent("You're not listening to anything currently.");
      }

    } catch (ParseException | SpotifyWebApiException | IOException e) {
      message.setContent("There was an error: " + e.getMessage());
      e.printStackTrace();
    }

    return message.build();
    
  }

}
