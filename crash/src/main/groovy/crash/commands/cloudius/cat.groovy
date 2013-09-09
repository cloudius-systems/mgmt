package crash.commands.cloudius

import org.crsh.cli.Argument
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.Command

// TODO: Allow multiple files to be specified

class cat {
  @Usage("concatenate files and print on standard output")
  @Command
  Object main(
  @Required @Argument String path
  ) {
    file = new File(getCurrentPath().getPath(), path)

    if (!file.exists()) {
      throw new ScriptException("no such file or directory")
    } else if (file.isDirectory()) {
      throw new ScriptException("is a directory")
    } else {
      buffer = new BufferedReader(new FileReader(file))
      try {
        line = buffer.readLine()

        while (line != null) {
          context.print("${line}\n")
          line = buffer.readLine()
        }
      } finally {
        buffer.close()
      }
    }
  }
}
