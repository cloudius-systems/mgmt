package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class head implements Completer {
  @Usage("output the first part of files")
  @Command
  void main(@Option(names=['c', 'bytes']) @Usage('print the first K bytes of each file; with the leading \'-\', print all but the last K bytes of each file') String nBytes,
            @Option(names=['n', 'lines']) @Usage('print the first K lines instead of the first 10; with the leading \'-\', print all but the last K lines of each file') String nLines,
            @Option(names=['q', 'quiet']) @Usage('never print headers giving file names') Boolean isQuiet,
            @Required @Argument(completer = head.class) List<String> paths) {

    long nFiles = paths.size()
    // Default max for number of bytes = 16 Exabytes
    reqBytes = nBytes ? Long.parseLong(nBytes) : Long.MAX_VALUE
    // Default number of lines = 10
    reqLines = nLines ? Long.parseLong(nLines) : 10;
    paths.each { path ->
        file = getResolvedPath(path);
        if (!file.exists()) {
          err.println("head: cannot open '${path}' for reading: No such file or directory")
          return
        } else if (file.isDirectory()) {
          err.println("head: error reading '${path}': Is a directory")
          return
        } else {
          if (nFiles > 1 && !isQuiet)
              out.println("==>" + path  + "<==")
          totalLines = 0;
          file.eachLine { totalLines++ }
          remainingLines = reqLines < 0 ? reqLines + totalLines : reqLines
          remainingBytes = reqBytes < 0 ? reqBytes + file.size() : reqBytes
          buffer = new BufferedReader(new FileReader(file))
          try {
            line = buffer.readLine()
            // Stop if we run out of lines, or either of the two limits are met
            while (line != null && remainingLines > 0 && remainingBytes > 0) {
              currBytes = line.size()
              out.println(line.substring(0, (int) (remainingBytes < currBytes ? remainingBytes : currBytes)))
              remainingLines--
              remainingBytes -= currBytes
              line = buffer.readLine()
            }
          } finally {
            buffer.close()
          }
       }
    }
  }

  @Override
  Completion complete(ParameterDescriptor parameterDescriptor, String s) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameterDescriptor, s
    )
  }
}
