package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage

class pwd {
  @Usage("print name of current working directory")
  @Command
  Object main() {
    out.print(currentPath)
  }
}
