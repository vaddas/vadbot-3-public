package vadbot;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VadBotPrintStream extends PrintStream {

  public VadBotPrintStream(OutputStream out) {
    super(out);
  }

  @Override
  public void println(String string) {
      
      LocalDateTime logTime = LocalDateTime.now();
    
      super.println("[" + logTime.format(DateTimeFormatter.ofPattern("HH:mm:ss MM-dd-uu")) + "] " + string);
  }
  
}
