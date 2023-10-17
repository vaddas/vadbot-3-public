package vadbot;

import static spark.Spark.*;

import java.io.InputStream;
import java.util.Scanner;

import spark.Spark;

public class VadBeacon {

  public interface AuthCodeCallback {
    void onAuthCodeReceived(String authCode, String state);
  }

  private static AuthCodeCallback callback;
  private static AuthCodeCallback fmCallback;

  public static void init(int localPort, int remotePort) {

    int port = VadBotMain.isLocal ? localPort : remotePort;

    port(port);

    get("/callback", (req, res) -> {
      String authCode = req.queryParams("code");
      String state = req.queryParams("state");

      if (callback != null && authCode != null)
        callback.onAuthCodeReceived(authCode, state);

      if (authCode != null) {
        return getSuccessWebsite(
            VadBotMain.jda.getSelfUser().getEffectiveAvatarUrl()
        );
      } else {
        return getFailureWebsite(
            VadBotMain.jda.getSelfUser().getEffectiveAvatarUrl()
        );
      }

    });

    get("/fmcallback", (req, res) -> {

      String token = req.queryParams("token");
      String discord = req.queryParams("discord");
      
      if (fmCallback != null && token != null)
        fmCallback.onAuthCodeReceived(token, discord);

      if (token != null) {
        return getSuccessWebsite(
            VadBotMain.jda.getSelfUser().getEffectiveAvatarUrl()
        );
      } else {
        return getFailureWebsite(
            VadBotMain.jda.getSelfUser().getEffectiveAvatarUrl()
        );
      }

    });

  }

  private static Object getFailureWebsite(String imgURL) {

    InputStream input = VadBeacon.class.getClassLoader()
        .getResourceAsStream("spotify_auth_failure.html");
    Scanner scanner = new Scanner(input);
    String site = scanner.useDelimiter("\\A").next();

    site = site.replace("vadbot-pfp", imgURL);

    scanner.close();

    return site;
  }

  public static void setCallback(AuthCodeCallback newCallback) {

    callback = newCallback;

  }

  public static void setFmCallback(AuthCodeCallback newCallback) {

    fmCallback = newCallback;

  }

  public static String getSuccessWebsite(String imgURL) {

    InputStream input = VadBeacon.class.getClassLoader()
        .getResourceAsStream("spotify_auth_success.html");
    Scanner scanner = new Scanner(input);
    String site = scanner.useDelimiter("\\A").next();

    site = site.replace("vadbot-pfp", imgURL);

    scanner.close();

    return site;

  }

}
