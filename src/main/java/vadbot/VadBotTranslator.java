package vadbot;

import buttons.AbstractVadBotButton;
import buttons.HelpButtons;
import buttons.ValQuoteButtons;
//import commands.AbstractVadBotCommand;
//import commands.Avatar;
//import commands.Ban;
//import commands.ChoiceMaker;
//import commands.CoinFlip;
//import commands.FlipaLot;
//import commands.HelpList;
//import commands.Kicker;
//import commands.LastFmHandler;
//import commands.Mario;
//import commands.PenisMaker;
//import commands.RunSQL;
//import commands.SayStuff;
//import commands.StatusShuffle;
//import commands.TestCommand;
//import commands.ValQuote;
//import commands.VoiceChannelManager;
import commands.*;
import stringmenus.AbstractVadBotStringMenu;
import stringmenus.HelpMenu;
import vadbot.RankManager.LevelsCommand;
import vadbot.RankManager.RankCommand;

public class VadBotTranslator {

  public static AbstractVadBotCommand translateCommand(String id) {

    AbstractVadBotCommand cmd = null;

    switch (id) {

    case "test":
      cmd = new TestCommand();
      break;
    case "coinflip":
      cmd = new CoinFlip();
      break;
    case "flipalot":
      cmd = new FlipaLot();
      break;
    case "valquote":
      cmd = new ValQuote();
      break;
    case "help":
      cmd = new HelpList();
      break;
    case "say":
      cmd = new SayStuff();
      break;
    case "status":
      cmd = new StatusShuffle();
      break;
    case "avatar":
      cmd = new Avatar();
      break;
    case "kick":
      cmd = new Kicker();
      break;
    case "ban":
      cmd = new Ban();
      break;
    case "penis":
    case "ponis":
      cmd = new PenisMaker();
      break;
    case "choose":
      cmd = new ChoiceMaker();
      break;
    case "join":
    case "leave":
    case "play":
    case "clearqueue":
    case "pause":
    case "resume":
    case "skip":
    case "nowplaying":
    case "queue":
    case "shuffle":
    case "loop":
      cmd = new VoiceChannelManager();
      break;
    case "sql":
      cmd = new RunSQL();
      break;
    case "rank":
      cmd = new RankCommand();
      break;
    case "levels":
      cmd = new LevelsCommand();
      break;
    case "login":
    case "fm":
    case "trends":
      cmd = new LastFmHandler();
      break;
    case "mgenerate":
    case "mcomplete":
    case "mstageprogress":
    case "mprogress":
      cmd = new Mario();
      break;
      
    case "website":
      cmd = new WebsiteSend();
      break;
    case "birthday":
    case "bdaynudge":
      cmd = new BirthdayManager();
      break;
    case "profile":
      cmd = new MemberProfileMaker();
      break;
    case "spotify":
      cmd = new SpotifyHandler();
      break;

    }

    return cmd;

  }

  public static AbstractVadBotButton translateButton(String id) {

    AbstractVadBotButton button = null;

    switch (id) {

    case "valquote":
      button = new ValQuoteButtons();
      break;
    case "help":
      button = new HelpButtons();
      break;

    }

    return button;

  }

  public static AbstractVadBotStringMenu translateMenu(String id) {

    AbstractVadBotStringMenu menu = null;

    switch (id) {

    case "help":
      menu = new HelpMenu();
      break;

    }

    return menu;

  }

}
