package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage

class java {
  @Usage("Java application launcher")
  @Command
  Object main() {
    // "unmatched" contains all of the params not matched by arguments/options
    // Passing it as-is to RunJava
    RunJava.main(unmatched.split(' '))
  }
}
