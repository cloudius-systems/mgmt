package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class mkdir implements Completer {
  @Usage("create directory")
  @Command
  void main(
  @Required @Argument(completer = mkdir.class) String path
  ) {
    File newdir = new File(path)
    if (!newdir.exists()) {
      out.println(newdir.mkdirs())
    } else {
      throw new ScriptException("Directory is already there")
    }
  }

  @Override
  Completion complete(ParameterDescriptor parameterDescriptor, String s) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameterDescriptor, s
    )
  }
}
