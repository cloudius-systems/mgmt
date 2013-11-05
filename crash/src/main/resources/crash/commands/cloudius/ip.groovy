package commands.cloudius

import com.cloudius.cli.command.OSvCommand
import com.cloudius.net.Route
import com.cloudius.util.ELFLoader
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Man
import org.crsh.cli.Usage
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class ip extends OSvCommand {
  @Usage("ip route")
  @Command
  void route(
    @Argument(completer = CommandCompleter)
      @Man("The command to execute. If no command is specified, print route table")
      String command,

    @Argument(completer = ArgumentsCompleter)
      @Man("List of arguments, depends on command")
      List<String> arguments
  ) {
    if (command == null) {
      def elfLoader = new ELFLoader()
      elfLoader.run("/tools/lsroute.so")
    } else {
      switch (command) {
        case "add":
          add(arguments)
          break;

        default:
          throw new ScriptException("Unknown command: ${command}")
      }
    }
  }

  public static void add(List<String> arguments) {
    if (arguments.size() == 3) {
      if (arguments[0] == "default" && arguments[1] == "gw") {
        Route.add_default(arguments[2])
      }
    } else {
      throw new ScriptException("Wrong arguments")
    }
  }

  public static class ArgumentsCompleter implements Completer {
    @Override
    Completion complete(ParameterDescriptor parameter, String prefix) throws Exception {
      complete(["default", "gw"], parameter, prefix).build()
    }
  }

  public static class CommandCompleter implements Completer {
    @Override
    Completion complete(ParameterDescriptor parameter, String prefix) throws Exception {
      complete(["add"], parameter, prefix).build()
    }
  }
}
