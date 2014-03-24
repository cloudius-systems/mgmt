package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class cd implements Completer {
  @Usage("change the current working directory")
  @Command
  public void main(
    @Argument(completer = cd.class) String path) {

    if (path == null) {
      if (unmatched == '-' && lastPath != null) {
        newPath = lastPath
      } else {
        newPath = new File('/')
      }
    } else if (path.startsWith('/')) {
      newPath = new File(path).getCanonicalFile()
    } else {
      newPath = new File(getCurrentPath().getPath(), path).getCanonicalFile()
    }

    if (!newPath.exists()) {
      throw new ScriptException("no such file or directory")
    } else if (!newPath.isDirectory()) {
      throw new ScriptException("not a directory")
    } else {
      setCurrentPath(newPath)
    }
  }

  @Override
  Completion complete(ParameterDescriptor parameter, String prefix) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameter, prefix
    )
  }
}

