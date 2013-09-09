package crash.commands.cloudius

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage

public class cd {
  @Usage("change the current working directory")
  @Command
  public Object main(
    @Argument String path) {

    if (path == null) {
      newPath = new File('/')
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
}

