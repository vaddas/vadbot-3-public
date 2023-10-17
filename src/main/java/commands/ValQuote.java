package commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import vadbot.ConstantIDs;

public class ValQuote implements AbstractVadBotCommand {

  private enum CHARACTER_TYPE {
    FICTIONAL, REAL
  };

  private static HashMap<String, ValQuote> allCharacters = new HashMap<>();

  private String name;
  private String imageURL;
  private String color;
  private CHARACTER_TYPE type;
  private ArrayList<String> quotes;

  public ValQuote() {
  }

  public ValQuote(
      String name,
      String imageURL,
      String color,
      boolean isFictional,
      ArrayList<String> quotes) {

    this.name = name;
    this.imageURL = imageURL;
    this.color = color;
    this.quotes = quotes;

    if (isFictional)
      type = CHARACTER_TYPE.FICTIONAL;
    else
      type = CHARACTER_TYPE.REAL;

  }

  public static void init() {

    ArrayList<String> phoenixQuotes = new ArrayList<>(
        Arrays.asList(
            "**Joke\'s over, you\'re DEAD!**",
            "Buy stuff, kaching, go SKRRRRRRRA!! Then we\'re done! yeh?",
            "SKRRRRRRA!!",
            "Careful now!",
            "I got you, bruv",
            "**Come on, let\'s GO!**"
        )
    );

    ArrayList<String> jettQuotes = new ArrayList<>(
        Arrays.asList(
            "There you are, you little shit!",
            "You? Outrun me?",
            "**Watch this!**",
            "**Get out of my way!**",
            "I got your backs, just, y\'know, from the front.",
            "Blocking sight.",
            "Wind, show me where to go.",
            "Oh- was that important?",
            "Yikes",
            "Let\'s get these idiots."
        )
    );

    ArrayList<String> breachQuotes = new ArrayList<>(
        Arrays.asList(
            "**OFF YOUR FEET!**",
            "**LETS GOOOOOOOOO!**",
            "Blinding!",
            "If you\'re not being shot at, you\'re doing something wrong",
            "Tak tak",
            "Come on! Get me in the line of fire.",
            "Dead",
            "HA! We bring enough body bags?",
            "Time to take these arms for a spin.",
            "Just swapping in new fusion cells... Okay! My arms are good!"
        )
    );

    ArrayList<String> cypherQuotes = new ArrayList<>(
        Arrays.asList(
            "Breach, Breach, I took a servo from your arms for my tripwire. Don\'t be angry Breach",
            "***I know exactly where you are.***",
            "*Where is everyone hiding?*",
            "One of my cameras is broken... oh, wait, it\'s fine.",
            "Deleted.",
            "Cage triggered.",
            "My eyes are down."
        )
    );

    ArrayList<String> killjoyQuotes = new ArrayList<>(
        Arrays.asList(
            "Swarm grenade out!",
            "Turret out.",
            "Alarm bot out!",
            "Recalling my bot!",
            "**Initiated!**",
            "**You should run.**",
            "Imagine if I died! Right now! So funny.",
            "If i\'m not stressed, you should not be stressed. I mean, look at me! Cool as a cucumber. Yeah that\'s the thing Americans say, right?",
            "Can someone, um, steal his eye? What? I want to see what\'s recorded.",
            "Breach, I wish you\'d take my extra reactor coolant. If your arms overheat out there, don\'t blame me",
            "Cypher, your homemade gadgets are so cute! Reminds me of some of my work in kindergarten.",
            "Everyone makes fun of German efficiency! Ha- haa! Just keep laughing as you use all my gear.",
            "You can trust my bots. They only malfunctioned that one time, and- honestly she won\'t miss that finger.",
            "Good, one step closer to being in pajamas."
        )
    );

    ArrayList<String> skyeQuotes = new ArrayList<>(
        Arrays.asList(
            "What\'s so special about that Killjoy\'s tech? I\'ve got visual sensors too. They\'re called eyes.",
            "If you see one of my creatures, it\'s a good thing alright? You\'ll figure out what they do eventually.",
            "I\'m no doctor, but I can patch a bullet hole or two. Let me know if you need healing!",
            "Don\'t sit down, we\'re not done yet.",
            "Heal up squad!",
            "Healing over here!",
            "Hawk out!",
            "I got you\'s, okay?",
            "**I\'ve got your trail.**",
            "**Seek them out!**",
            "They\'ve got a Brimstone over there! That man\'s a fossil. Put him in the ground where he belongs!",
            "I feel something over there. Can\'t place it. Like us, but different.",
            "We move as a unit, okay? Last thing I need is some HERO messing things up."
        )
    );

    ArrayList<String> sageQuotes = new ArrayList<>(
        Arrays.asList(
            "**You will not kill my allies!**",
            "Stand tall. We are VALORANT, we are fighters!",
            "Cypher removed.",
            "Wall raised.",
            "Healing you.",
            "**Your duty is not over!**"
        )
    );

    ArrayList<String> omenQuotes = new ArrayList<>(
        Arrays.asList(
            "mmmmmmmmmmmmmmmmmmmmmm",
            "Stealing sight.",
            "Shadows traveling.",
            "I feel the strain... block it out, Omen.",
            "I will be their nightmare.",
            "Snuff them out.",
            "Blind and dead.",
            "Cover going out.",
            "Watch them run.",
            "**SCATTAAA!**",
            "I need this.",
            "Reyna, the souls you consume, do you hear their voices as I do?"
        )
    );

    ArrayList<String> razeQuotes = new ArrayList<>(
        Arrays.asList(
            "**FYA IN ZE HOLE!**",
            "Cypher I\'m so sorry about breaking all your stuff. I mean, I won\'t stop, but I am sorry.",
            "None of my stuff has malfunctioned yet, can we celebrate that?",
            "Paint? Check. Charges? Check. Brakes? Nowhere in sight",
            "They want to dance? Ha, I\'ll lead.",
            "I promise I\'ll get them, might take down a city block along the way but a win\'s a win",
            "Okay I only have like 78 grenades left, hopefully that\'s enough.",
            "Bomb buddy out.",
            "Go get \'em!",
            "Satchel out!",
            "**HERE COMES THE PARTY!**"
        )
    );

    ArrayList<String> reynaQuotes = new ArrayList<>(
        Arrays.asList(
            "**The hunt begins.**",
            "**They will cower!**",
            "I\'ve stood over so many corpses. A few more does not matter.",
            "They want to die, let them.",
            "If you\'ve never seen a massacre, this is what it looks like.",
            "We win, we survive.",
            "Their lives are so meaningless, might as well give them to me.",
            "They don\'t have a single life worth saving.",
            "I remember the machines Killjoy made, what they did to- nevermind. It does not matter.",
            "See you on the front lines Phoenix, save some enemies for me.",
            "Cutting their vision."
        )
    );

    ArrayList<String> sovaQuotes = new ArrayList<>(
        Arrays.asList(
            "Shock dart",
            "**I AM THE HUNTER!**",
            "**NOWHERE TO RUN!**",
            "Revealing area...",
            "Found them.",
            "There they are.",
            "Deploying drone."
        )
    );

    ArrayList<String> brimstoneQuotes = new ArrayList<>(
        Arrays.asList(
            "Smokes down!",
            "Molly!",
            "**Open up the sky!**",
            "**Prepare for hellfire!**"
        )
    );

    ArrayList<String> viperQuotes = new ArrayList<>(
        Arrays.asList(
            "**Don\'t get in my way.**",
            "Poison orb emitting.",
            "Poison\'s down",
            "Toxins going up.",
            "Toxin screen down.",
            "cum",
            "Should I use a stick next time?",
            "If any of you die, I lose a bet with Brimstone, so don\'t embarrass me please.",
            "Sage, you\'re the only one who can keep us alive. Don\'t fail me now like you failed me then.",
            "You have the ability to rift walk and you stay here? What a shame... there are so many worlds out there.",
            "(to Sage) NEVER. EVER. ASSUME YOU CAN HELP ME. You can\'t help me. You can\'t help them.",
            "**Welcome to my world!**"
        )
    );

    ArrayList<String> yoruQuotes = new ArrayList<>(
        Arrays.asList(
            "*Who\'s next?*",
            "Cutting through.",
            "I\'ll handle this.",
            "I don\'t care if there are five of them or fifty of them, let\'s go.",
            "Omen, let\'s teleport and spook those guys. We\'ll make this a real horror show.",
            "Anyone else think they can kill a God?",
            "The Enemy omen is like me. He\'ll try to get behind us. Watch your backs.",
            "I\'ll fight anyone, I\'ll fight everyone.",
            "sanks",
            "If I kill all the enemies myself, I apologise. I have trouble sharing."
        )
    );

    ArrayList<String> astraQuotes = new ArrayList<>(
        Arrays.asList(
            "You are divided!",
            "Concealment!",
            "Pulling them in!",
            "*random humming*"
        )
    );

    ArrayList<String> kayoQuotes = new ArrayList<>(
        Arrays.asList(
            "Negative!",
            "Shutting them down!",
            "Suppressing!",
            "Knife deployed!",
            "**No one walks away!**",
            "**You! Are! Powerless!**",
            "Catch a breath. Not that I need to, of course.",
            "Activating kill mode... That\'s a joke. Kill mode is default.",
            "Maybe we get the spike. Just a thought.",
            "Next time, mind the heavy machinery.",
            "I know what they\'re thinking...aimbot.",
            "That was badass.",
            "Breach, after this, billiards. I like taking your money.",
            "Brimstone, I\'d hug you but it would kill you. I\'m serious, no hugs."
        )
    );

    ArrayList<String> chamberQuotes = new ArrayList<>(
        Arrays.asList(
            "Excusez-moi!",
            "You have good taste, my friend.",
            "They are so dead!",
            "You want to play? Let\'s play.",
            "Okay, last looks. You all look wonderful. Me, I always look good. We are ready.",
            "I must say, I like what they have done with the place.",
            "Oh no, invaders here to take our radianite! LOL!!!! I\'m sorry, let\'s go shoot them.",
            "Weapon choice, it is so personal, no? You pick a gun and it tells me who you are.",
            "These new friends of ours are not too friendly.",
            "Mesdames et messieurs... the spike!",
            "It\'s a shame you had to die, but you are not part of my bigger picture",
            "I would apologize to them but alas, they are dead.",
            "Oh, I am not cleaning this up!",
            "Be careful with my secrets, Cypher. It is better, I think, if we stay friends.",
            "KAY/O, you are so industrial, it\'s a good look, but if you need something more upscale, I have many ideas.",
            "Do not worry, my radiant friends. I will unplug their talking toaster."
        )
    );

    ArrayList<String> announcerQuotes = new ArrayList<>(
        Arrays.asList(
            "Match found.",
            "Spike planted.",
            "Ten seconds remaining.",
            "Last round in the half.",
            "Attackers win!"
        )
    );

    ArrayList<String> mathijsQuotes = new ArrayList<>(
        Arrays.asList(
            "I\'m pretty SPLIT on this map.",
            "It\'s literally just because we\'re playing on Icebox.",
            "Why am I doing so bad today?! Oh my god.",
            "*quiet crying*",
            "gg ez",
            "I\'m only gonna play one game.",
            "*the map is fracture*\nARE YOU FUCKING KIDDING ME ASLHJKFHS DLKJHSD JKFN"
        )
    );

    ArrayList<String> wiooQuotes = new ArrayList<>(
        Arrays.asList(
            "AUUUUUUUGGHHHHHHHHHHHHHHHHHH",
            "WOCKYYY",
            "WHOOOP",
            "*spins* i\'m rotating",
            "Do you like to eat cum?",
            "don\'t worry guys",
            "*is last player standing*\nGUYS????????",
            "**violent angry shouting in german accent**"
        )
    );

    ArrayList<String> natalieQuotes = new ArrayList<>(
        Arrays.asList(
            "*rapid intense breathing*",
            "Don\'t worry guys, it\'s fine, we\'re playing golf."
        )
    );

    ArrayList<String> wheelQuotes = new ArrayList<>(
        Arrays.asList(
            "yar",
            "lol you guys suck <3",
            "*unrelated singing*",
            "natalie just get 4 ult orbs and revive me",
            "*when using a smoke is mentioned* \nmikaela?"
        )
    );

    ArrayList<String> jimmyQuotes = new ArrayList<>(
        Arrays.asList(
            "I am the dedicated spike, and you are the dedicated bitch, and Will is the designated... Yoru."
        )
    );

    ArrayList<String> calvinQuotes = new ArrayList<>(
        Arrays.asList(
            "run no time",
            "Ah yes, I am MVP once again.",
            "gg ez",
            "swoopeek???",
            "This guy\'s gotta be hacking",
            "Alright just activate my wall real quick"
        )
    );

    ValQuote phoenix = new ValQuote(
        "Phoenix",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967151869329468/pheonix.jpg",
        "#B53600", true, phoenixQuotes
    );
    ValQuote jett = new ValQuote(
        "Jett",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967217686216714/jett.png",
        "#B6F4FC", true, jettQuotes
    );
    ValQuote breach = new ValQuote(
        "Breach",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967016406679592/breach.jpg",
        "#ff4203", true, breachQuotes
    );
    ValQuote cypher = new ValQuote(
        "Cypher",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967089088987136/cypher.jpg",
        "#575f69", true, cypherQuotes
    );
    ValQuote killjoy = new ValQuote(
        "Killjoy",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967307259772928/killjoy.png",
        "#e1ff00", true, killjoyQuotes
    );
    ValQuote skye = new ValQuote(
        "Skye",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967382875471902/skye.png",
        "#83f763", true, skyeQuotes
    );
    ValQuote sage = new ValQuote(
        "Sage",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967381901606963/sage.png",
        "#45ffec", true, sageQuotes
    );
    ValQuote omen = new ValQuote(
        "Omen",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967327522586674/omen.png",
        "#140142", true, omenQuotes
    );
    ValQuote raze = new ValQuote(
        "Raze",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967156290650112/raze.jpg",
        "#ffb300", true, razeQuotes
    );
    ValQuote reyna = new ValQuote(
        "Reyna",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967170546434098/reyna.jpg",
        "#91077d", true, reynaQuotes
    );
    ValQuote sova = new ValQuote(
        "Sova",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967218395316264/sova.jpg",
        "#5c82ff", true, sovaQuotes
    );
    ValQuote brimstone = new ValQuote(
        "Brimstone",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967154293768252/brimstone.jpg",
        "#ffa600", true, brimstoneQuotes
    );
    ValQuote viper = new ValQuote(
        "Viper",
        "https://cdn.discordapp.com/attachments/813965778084036618/813967252126433312/viper.jpg",
        "#63e31e", true, viperQuotes
    );
    ValQuote yoru = new ValQuote(
        "Yoru",
        "https://cdn.discordapp.com/attachments/813965778084036618/819958124362334259/unknown.png",
        "#0531f5", true, yoruQuotes
    );
    ValQuote astra = new ValQuote(
        "Astra",
        "https://cdn.discordapp.com/attachments/813965778084036618/833082105025658880/unknown.png",
        "#802cca", true, astraQuotes
    );
    ValQuote kayo = new ValQuote(
        "KAY/O",
        "https://cdn.discordapp.com/attachments/813965778084036618/879247988374011934/bo-P8X6u_400x400.png",
        "#9376f5", true, kayoQuotes
    );
    ValQuote chamber = new ValQuote(
        "Chamber",
        "https://media.discordapp.net/attachments/813965778084036618/915023073093238794/unknown.png",
        "#b50b44", true, chamberQuotes
    );
    ValQuote announcer = new ValQuote(
        "Announcer",
        "https://studio.cults3d.com/4QqRV9kLYYEuw9ur_X3yjQl1sjk=/516x516/https://files.cults3d.com/uploaders/15024335/illustration-file/a86d53e4-2bd9-4a8f-9550-986686c3131a/gi0mAjIh_400x400.png",
        "#949494", true, announcerQuotes
    );

    ValQuote mathijs = new ValQuote(
        "Mathijs", "", "#ff0000", false, mathijsQuotes
    );
    ValQuote wioo = new ValQuote("Wioo", "", "#920000", false, wiooQuotes);
    ValQuote natalie = new ValQuote(
        "Natalie", "", "#ff82fd", false, natalieQuotes
    );
    ValQuote wheel = new ValQuote("Wheel", "", "#0531f5", false, wheelQuotes);
    ValQuote jimmy = new ValQuote("Jimmy", "", "#5c0000", false, jimmyQuotes);
    ValQuote calvin = new ValQuote(
        "Calvin", "", "#ff9100", false, calvinQuotes
    );

    allCharacters.put("phoenix", phoenix);
    allCharacters.put("jett", jett);
    allCharacters.put("breach", breach);
    allCharacters.put("cypher", cypher);
    allCharacters.put("killjoy", killjoy);
    allCharacters.put("skye", skye);
    allCharacters.put("sage", sage);
    allCharacters.put("omen", omen);
    allCharacters.put("raze", raze);
    allCharacters.put("reyna", reyna);
    allCharacters.put("sova", sova);
    allCharacters.put("brimstone", brimstone);
    allCharacters.put("viper", viper);
    allCharacters.put("yoru", yoru);
    allCharacters.put("astra", astra);
    allCharacters.put("kayo", kayo);
    allCharacters.put("kay/o", kayo);
    allCharacters.put("chamber", chamber);
    allCharacters.put("announcer", announcer);
    allCharacters.put("mathijs", mathijs);
    allCharacters.put("wioo", wioo);
    allCharacters.put("natalie", natalie);
    allCharacters.put("wheel", wheel);
    allCharacters.put("jimmy", jimmy);
    allCharacters.put("calvin", calvin);

  }

  public static EmbedBuilder makeEmbed(String ename, JDA jda)
      throws IllegalArgumentException {

    String name = ename.toLowerCase();

    EmbedBuilder embed = new EmbedBuilder();

    if (name.isEmpty())
      name = randomCharacter();

    ValQuote v = allCharacters.get(name);

    if (v != null) {

      embed.setTitle(v.name);
      embed.setDescription(
          v.quotes.get((int) (Math.random() * v.quotes.size()))
      );
      embed.setColor(Color.decode(v.color));

      if (v.type == CHARACTER_TYPE.FICTIONAL)
        embed.setThumbnail(v.imageURL);
      else
        embed.setThumbnail(getAvatarURL(name, jda));

    } else {

      throw new IllegalArgumentException();

    }

    return embed;

  }

  public static EmbedBuilder makeEmbed(OptionMapping nameOption, JDA jda) {

    if (nameOption != null)
      return makeEmbed(nameOption.getAsString(), jda);
    else
      return makeEmbed(randomCharacter(), jda);

  }

  public static EmbedBuilder allQuotes(String character, JDA jda) {

    EmbedBuilder embed = new EmbedBuilder();

    ValQuote v = allCharacters.get(character);

    embed.setTitle(v.name);

    String quoteList = "";
    for (String q : v.quotes)
      quoteList += q + "\n\n";

    embed.setDescription(quoteList);
    embed.setColor(Color.decode(v.color));

    if (v.type == CHARACTER_TYPE.FICTIONAL)
      embed.setThumbnail(v.imageURL);
    else
      embed.setThumbnail(getAvatarURL(character, jda));

    return embed;

  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {

    OptionMapping character = event.getOption("character");
    EmbedBuilder embed;
    MessageCreateData valquoteMessage;
    try {
      embed = makeEmbed(character, event.getJDA());
      valquoteMessage = new MessageCreateBuilder().setEmbeds(embed.build())
          .setActionRow(
              Button.primary("valquote-list", "List"),
              Button.primary("valquote-newquote", "Different Quote"),
              Button.primary("valquote-newcharacter", "Different Character")
          )
          .build();
    } catch (IllegalArgumentException e) {
      embed = (new EmbedBuilder())
          .setDescription("Please enter a valid character.")
          .setColor(Color.RED);
      valquoteMessage = new MessageCreateBuilder().setEmbeds(embed.build())
          .build();
    }

    event.getHook().sendMessage(valquoteMessage).queue();

  }

  private static String randomCharacter() {

    int size = allCharacters.keySet().size();
    int item = new Random().nextInt(size);
    int i = 0;
    for (String n : allCharacters.keySet()) {

      if (i == item)
        return n;
      i++;

    }

    return null;

  }

  private static String getAvatarURL(String person, JDA jda) {

    String avatarURL = "";
    Guild g = jda.getGuildById(ConstantIDs.gifSpam);
    switch (person) {

    case "mathijs":
      avatarURL = g.getMemberById(ConstantIDs.mathijs).getEffectiveAvatarUrl();
      break;
    case "wioo":
      avatarURL = g.getMemberById(ConstantIDs.wioo).getEffectiveAvatarUrl();
      break;
    case "natalie":
      avatarURL = g.getMemberById(ConstantIDs.natalie).getEffectiveAvatarUrl();
      break;
    case "wheel":
      avatarURL = g.getMemberById(ConstantIDs.wheel).getEffectiveAvatarUrl();
      break;
    case "jimmy":
      avatarURL = g.getMemberById(ConstantIDs.jimmy).getEffectiveAvatarUrl();
      break;
    case "calvin":
      avatarURL = g.getMemberById(ConstantIDs.calvin).getEffectiveAvatarUrl();
    }

    return avatarURL;

  }

}
