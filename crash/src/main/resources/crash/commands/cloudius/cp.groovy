package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.apache.commons.io.FileUtils
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Option
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class cp implements Completer {
  @Command
  @Usage("copy files")
  void main(@Option(names=['t', 'target-directory'], completer = cp.class) @Usage('copy all source arguments into directory') String targetDirectory,
            @Option(names=['T', 'no-target-directory']) @Usage('treat dest as a normal file') Boolean noTargetDirectory,
            @Option(names=['R', 'r', 'recursive']) @Usage('copy directories recursively') Boolean isRecurisve,
            @Argument(completer = cp.class) @Usage('list of sources') List<String> src,
            @Argument(completer = cp.class) @Usage('destination') String dst) {

    if (targetDirectory != null && noTargetDirectory) {
      throw new ScriptException("-t and -T cannot be combined")
    }

    if (dst == null || (targetDirectory == null && src.size() == 0)) {
      throw new ScriptException("Missing arguments")
    }

    def sources = src.collect { [file: getResolvedPath(it) as File, lbl: it] }
    def dest = [file: getResolvedPath(dst) as File, lbl: dst]

    if (targetDirectory != null) {
      sources << dest
      dest = [file: getResolvedPath(targetDirectory) as File, lbl: targetDirectory]

      if (!dest.file.isDirectory()) {
        throw new ScriptException("target '${dest.lbl}' is not a directory" as String)
      }
    }

    if (sources.size() > 1 && !dest.file.isDirectory()) {
      throw new ScriptException("No such directory or file ('${dest.lbl}')" as String)
    }

    sources.each { source ->
      def tmpDest = dest.clone()

      if (source.file.isDirectory()) {
        if (!isRecurisve) {
          err.println("cp: omitting directory '${source.lbl}'" as String)
          return // continue
        }

        if (tmpDest.file.exists() && !tmpDest.file.isDirectory()) {
          err.println("cp: cannot overwrite non-directory '${tmpDest.lbl}' with directory '${source.lbl}'" as String)
          return // continue
        }

        if (tmpDest.file.exists() && !noTargetDirectory) {
          FileUtils.copyDirectoryToDirectory(source.file, tmpDest.file)
        } else {
          FileUtils.copyDirectory(source.file, tmpDest.file)
        }
      } else {
        if (tmpDest.file.isDirectory() && noTargetDirectory) {
          err.println("cp: cannot overwrite directory '${tmpDest.lbl}' with non-directory '${source.lbl}'" as String)
          return // continue
        }

        if (!tmpDest.file.getParentFile().exists()) {
          err.println("cp: ${tmpDest.file.getParent()}: No such file or directory" as String)
          return // continue
        }

        if (tmpDest.file.isDirectory()) {
          FileUtils.copyFileToDirectory(source.file, tmpDest.file)
        } else {
          FileUtils.copyFile(source.file, tmpDest.file as File)
        }
      }
    }
  }

  @Override
  Completion complete(ParameterDescriptor parameter, String prefix) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameter, prefix
    )
  }
}