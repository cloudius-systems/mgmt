package crash.commands.cloudius

import com.cloudius.cli.command.OSvCommand
import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

// TODO: Implement ls for multiple paths

class ls extends OSvCommand implements Completer {
  @Usage("list directory contents")
  @Command
  String main(
    @Argument(completer = ls.class) String path
  ) {
    pathToList = getResolvedPath(path == null ? '.' : path)
    if (!pathToList.exists()) {
        throw new ScriptException("no such file or directory")
    }
    files = pathToList.isDirectory() ? pathToList.listFiles() : [pathToList]
    itemsToColumns(files.collect({ it.isDirectory() ? it.getName() + "/" : it.getName() }), context.width)
  }

  @Override
  Completion complete(ParameterDescriptor parameter, String prefix) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameter, prefix
    )
  }
}
