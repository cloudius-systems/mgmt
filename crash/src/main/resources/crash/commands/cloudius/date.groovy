package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage

class date {
  @Usage("date")
  @Command
  Object main() {
    Date date = new Date()
    out.println(date.toString())
  }
}

