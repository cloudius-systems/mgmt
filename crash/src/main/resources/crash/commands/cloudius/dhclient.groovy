package crash.commands.cloudius

import org.crsh.cli.Command
import com.cloudius.net.DHCP
import org.crsh.cli.Usage

@Usage("start the DHCP client")
class dhclient {
  @Command
  Object main() {
    DHCP.dhcp_start()
  }
}
