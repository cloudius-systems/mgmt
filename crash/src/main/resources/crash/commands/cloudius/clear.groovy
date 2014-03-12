package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage

class clear {
  @Usage("clear screen")
  @Command
  Object main() {
    out.print(String.format("\033[2J"))
  }
}
