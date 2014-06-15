package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class rm implements Completer {
  @Usage("delete file or directory")
  @Command
  void main(@Option(names=['R', 'r', 'recursive']) @Usage('remove directories and their contents recursively') Boolean isRecursive,
            @Option(names=['f', 'force']) @Usage('ignore nonexistent files and arguments, never prompt') Boolean isForceful,
            @Option(names=['v', 'verbose']) @Usage('explain what is being done') Boolean isVerbose,
            @Option(names=['i', 'interactive']) @Usage('prompt before every removal') Boolean isInteractive,
            @Required @Argument(completer = rm.class) List<String> paths) {

    def deleteStuff

    deleteStuff = {
      Boolean isDir = it.isDirectory()
      String reply = null
      if (!it.exists()) {
        if (!isForceful) {
          err.println("rm: cannot remove '${it}': No such file or directory")
        }
        return
      }
      if (isDir) {
        // Don't delete directory if -r is not specified
        if (!isRecursive) {
          err.println("rm: omitting directory '${it}'")
          return
        }
        if (!isForceful && isInteractive) {
          reply = context.readLine("rm: descend into directory '${it}'? ", true)
          if (!(reply.size() > 0 &&
                (reply.charAt(0) == 'y' || reply.charAt(0) == 'Y'))) {
            return
          }
        }
        // Recursively delete each File object in this directory (-r is specified)
        it.eachFile(deleteStuff)
      }

      if (!isForceful && isInteractive) {
        reply = context.readLine("rm: remove " +
                                 (isDir ? "directory" : "") + " '${it}'? ", true)
        if (!(reply.size() > 0
            && (reply.charAt(0) == 'y' || reply.charAt(0) == 'Y'))) {
          return
        }
        /* Don't delete directory if it's contents were not deleted
         * Control reaches here when using interactive + recursive mode, user has
         * left some files inside a directory undeleted */
        if (isDir && isRecursive && it.list().length > 0) {
          err.println("rm: cannot remove '${it}': Directory not empty")
          return
        }
      }

      if (it.delete()) {
        // -f overrides everything except -v
        if (isVerbose) {
          out.println("removed " + (isDir ? "directory" : "") + " '${it}'")
        }
      } else if (!isForceful) {
        err.println("rm: cannot remove '${it}'")
      }
    }

    paths.each { path ->
      deleteStuff(getResolvedPath(path))
    }

  }

  @Override
  Completion complete(ParameterDescriptor parameterDescriptor, String s) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameterDescriptor, s
    )
  }
}
