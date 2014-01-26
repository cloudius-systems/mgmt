package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.CRaSHCommand

@Usage("Alias for 'thread top'")
class top extends CRaSHCommand {
  @Command
  void main() {
    execute('thread top')
  }
}
