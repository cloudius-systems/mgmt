package crash.commands.cloudius

import com.cloudius.cli.command.OSvCommand
import com.cloudius.cli.util.OSvAPI
import com.cloudius.cli.util.SizeFormatter
import com.cloudius.cli.util.TextHelper
import org.crsh.cli.Command
import org.crsh.cli.Option
import org.crsh.cli.Usage
import org.crsh.text.ui.UIBuilder

@Usage("Display amount of free and used memory in the system")
public class free extends OSvCommand {
  @Command
  Object main(
      @Option(names=['b']) @Usage("Display the amount of memory in bytes.") Boolean isB,
      @Option(names=['k']) @Usage("Display the amount of memory in kilobytes. This is the default.") Boolean isKb,
      @Option(names=['m']) @Usage("Display the amount of memory in megabytes.") Boolean isMb,
      @Option(names=['g']) @Usage("Display the amount of memory in gigabytes.") Boolean isGb,
      @Option(names=['H']) @Usage("Show all output fields automatically scaled to shortest three digit unit and display the units of print out.") Boolean isH) {
    int total = Integer.parseInt(OSvAPI.get("/os/memory/total"))
    int free  = Integer.parseInt(OSvAPI.get("/os/memory/free"))

    def unit = SizeFormatter.toSizeUnit(isB, isKb, isMb, isGb, false, isH)
    def data = TextHelper.leftPadColumns([
        ["", "total", "used", "free"],
        ["Mem:",
            SizeFormatter.sizeFormatter(total, unit),
            SizeFormatter.sizeFormatter(total - free, unit),
            SizeFormatter.sizeFormatter(free, unit)]
    ])

    return new UIBuilder().table(rightCellPadding:1) {
      data.each { r ->
        row {
          r.each { c ->
            label(c)
          }
        }
      }
    }
  }
}
