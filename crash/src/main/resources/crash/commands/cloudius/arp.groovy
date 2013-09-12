package crash.commands.cloudius

import com.cloudius.cli.util.Networking
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required

class arp {
  @Command
  Object main(
    @Argument @Required ifname,
    @Argument @Required macaddr,
    @Argument @Required ip) {
    Networking.arp_add(ifname, macaddr, ip)
  }
}
