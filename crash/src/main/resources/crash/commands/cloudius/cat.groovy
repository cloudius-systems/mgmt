package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

// TODO: Allow multiple files to be specified

class cat implements Completer {
  @Usage("concatenate files and print on standard output")
  @Command
  void main(
  @Required @Argument(completer = cat.class) String path
  ) {
    file = getResolvedPath(path)

    if (!file.exists()) {
      throw new ScriptException("no such file or directory")
    } else if (file.isDirectory()) {
      throw new ScriptException("is a directory")
    } else {
      buffer = new BufferedReader(new FileReader(file))
      try {
        line = buffer.readLine()

        while (line != null) {
          out.print(line)
          line = buffer.readLine()

          // Print the new line only if we have more lines
          if (line != null) {
            out.println()
          }
        }
      } finally {
        buffer.close()
      }
    }
  }

  @Override
  Completion complete(ParameterDescriptor parameterDescriptor, String s) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameterDescriptor, s
    )
  }
}
