package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import com.cloudius.util.MD5
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class md5sum implements Completer {
  @Command
  Object main(
    @Required @Argument(completer = md5sum.class) String path) {
    file = getResolvedPath(path)
    if (!file.exists()) {
        throw new ScriptException("no such file or directory")
    }
    MD5.md5(file.getPath())
  }

  @Override
  Completion complete(ParameterDescriptor parameterDescriptor, String s) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameterDescriptor, s
    )
  }
}
