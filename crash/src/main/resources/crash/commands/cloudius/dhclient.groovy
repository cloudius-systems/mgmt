package crash.commands.cloudius

import org.crsh.cli.Command
import com.cloudius.net.DHCP

class dhclient {
  @Command
  Object main() {
    DHCP.dhcp_start()
  }
}
