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

class rm implements Completer {
  @Usage("delete file or directory")
  @Command
  void main(
  @Required @Argument(completer = rm.class) String path
  ) {
    file = getResolvedPath(path)

    if (!file.exists()) {
      throw new ScriptException("no such file or directory")
    }
    if (file.isDirectory()) {
      if (!file.delete()) {
        throw new ScriptException("cannot delete directory")
      }
      out.println("directory deleted")
    }
    if (file.isFile()) {
      if (!file.delete()) {
        throw new ScriptException("cannot delete file")
      }
      out.println("file deleted")
    }
  }

  @Override
  Completion complete(ParameterDescriptor parameterDescriptor, String s) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameterDescriptor, s
    )
  }
}
