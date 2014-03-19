package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage

class sleep {
  @Usage("sleep for number of seconds ")
  @Command
  void main(
  @Required @Argument(completer = sleep.class) int time
  ) {
    Thread.sleep(time)
  }
}
