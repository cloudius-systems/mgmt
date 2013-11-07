package crash.commands.cloudius

import com.cloudius.util.MD5
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required

class md5sum {
  @Command
  Object main(
    @Required @Argument String path) {
    file = getResolvedPath(path)
    if (!file.exists()) {
        throw new ScriptException("no such file or directory")
    }
    MD5.md5(file.getPath())
  }
}
