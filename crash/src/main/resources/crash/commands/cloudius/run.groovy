package crash.commands.cloudius

import com.cloudius.cli.command.OSvCommand
import com.cloudius.cli.completers.PathCompleter
import com.cloudius.cli.util.ELFLoader
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

@Usage("loads an ELF and runs it")
class run extends OSvCommand implements Completer {
  @Command
  void main(@Argument(completer = run.class) @Usage("the ELF file to load") @Required String path) {
    def file = getResolvedPath(path)

    if (!file.exists()) {
      throw new ScriptException("file not found")
    } else if (!file.isFile()) {
      throw new ScriptException("not a file")
    }

    daemonizeIfNeeded(unmatched, { String modUnmatched ->
      def command = [file.getCanonicalPath()]

      if (modUnmatched.size() > 0)
          command.addAll(modUnmatched.split(" "))

      def elfLoader = new ELFLoader()
      if (elfLoader.run(command as String[])) {
        out.println("run: finished with exitcode ${elfLoader.lastExitCode()}")
      } else {
        out.println("run: failed to execute ${path}")
      }
    })
  }

  @Override
  Completion complete(ParameterDescriptor parameter, String prefix) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameter, prefix
    )
  }
}
