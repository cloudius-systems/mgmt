package crash.commands.cloudius

import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion
import org.apache.commons.io.FileUtils
import java.util.Formatter

class df implements Completer {

  @Usage("report disk space usage")
  @Command
  Object main(
    @Option(names=['k']) @Usage("kilo bytes") Boolean isKb,
    @Option(names=['m']) @Usage("mega bytes") Boolean isMb,
    @Option(names=['g']) @Usage("human readable format") Boolean isHr,
    @Argument(completer = df.class) String path) {

    boolean mb = isMb.asBoolean()
    boolean hr = isHr.asBoolean()

    if (path == null) {
      f = new File("/")
    } else {
      f = getResolvedPath(path)
    }
    if (!f.exists()) {
      throw new ScriptException("no such file or directory")
    }

    long total = f.getTotalSpace()
    // 0L if this abstract pathname does not name a partition
    if (total == 0) {
      throw new ScriptException("doesn't name a partition")
    }

    long free   = f.getFreeSpace()
    long used   = total - free
    float ratio = used * 100 / total

    out.println(String.format("%16s%16s%16s%16s\n", "Size", "Used", "Avail", "Used%"))
    out.println(String.format("%16s%16s%16s%15.0f%%", sizeFormatter(total, mb, hr), sizeFormatter(used, mb, hr), sizeFormatter(free, mb, hr), ratio))
  }

  public static String sizeFormatter(long res, boolean mb, boolean hr) {
    String ret
    Formatter fmt = new Formatter()

    if (hr) {
      if (res > FileUtils.ONE_GB) {
        ret = fmt.format("%.1f%s", res / FileUtils.ONE_GB, "G")
      } else if (res > FileUtils.ONE_MB) {
        ret = fmt.format("%.1f%s", res / FileUtils.ONE_MB, "M")
      } else {
        ret = fmt.format("%.1f%s", res / FileUtils.ONE_KB, "K")
      }
    } else if (mb) {
      ret = fmt.format("%.0f", res / FileUtils.ONE_MB)
    } else {
      ret = fmt.format("%.0f", res / FileUtils.ONE_KB)
    }

    return ret
  }

  @Override
  Completion complete(ParameterDescriptor parameter, String s) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
      parameter, s
    )
  }
}
