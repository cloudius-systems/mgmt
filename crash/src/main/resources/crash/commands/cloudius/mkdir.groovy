package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Option
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
  @Option(names=['p', 'parents']) @Usage('make parent directories as needed') Boolean withParents,
  @Required @Argument(completer = mkdir.class) List<String> paths
  ) {
    paths.each { path ->
      def newdir = getResolvedPath(path)
      if (newdir.exists()) {
        err.println("cannot create directory '${path}': File exists")
      }

      withParents ? newdir.mkdirs() : newdir.mkdir()
    }
  }

  @Override
  Completion complete(ParameterDescriptor parameterDescriptor, String s) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameterDescriptor, s
    )
  }
}
