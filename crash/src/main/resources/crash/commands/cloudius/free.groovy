package crash.commands.cloudius

import com.cloudius.cli.command.FormatSizeOptions
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
      @FormatSizeOptions.B Boolean isB,
      @FormatSizeOptions.Kb Boolean isKb,
      @FormatSizeOptions.Mb Boolean isMb,
      @FormatSizeOptions.Gb Boolean isGb,
      @FormatSizeOptions.Human Boolean isH) {
    long total = Long.parseLong(OSvAPI.get("/os/memory/total"))
    long free  = Long.parseLong(OSvAPI.get("/os/memory/free"))

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
