package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class cat implements Completer {
  @Usage("concatenate files and print on standard output")
  @Command
  void main(
  @Required @Argument(completer = cat.class) List<String> paths
  ) {
    paths.each { path ->
      file = getResolvedPath(path)

      if (!file.exists()) {
        err.println("cat: '${file}:' No such file or directory")
        return
      } else if (file.isDirectory()) {
        err.println("cat: '${file}:' Is a directory")
        return
      } else {
        buffer = new BufferedReader(new FileReader(file))
        try {
          character = buffer.read()
          /* Read character by character, no need to worry
           * about newlines or carriage returns */
          while (character != -1) {
            out.print((char)character)
            character = buffer.read()
          }
        } finally {
          buffer.close()
        }
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
