package crash.commands.cloudius

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage

class java {
  @Usage("Java application launcher")
  @Command
  Object main(
    @Required @Argument List<String> argv) {
    RunJava.main(argv.toArray())
  }
}
