package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage
import io.osv.RunJava

class java {
  @Usage("Java application launcher")
  @Command
  void main() {
    // "unmatched" contains all of the params not matched by arguments/options
    // Passing it as-is to RunJava
    if (unmatched.trim().size() > 0) {
      RunJava.main(unmatched.trim().split(' '))
    }
  }
}
