package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage

class ls {
  @Usage("list directory contents")
  @Command
  Object main() {
    context.print(getCurrentPath().listFiles().collect({
      it.getName()
    }).join('\t'))
  }
}
