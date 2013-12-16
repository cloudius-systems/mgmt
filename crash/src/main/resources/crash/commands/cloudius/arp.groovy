package crash.commands.cloudius

import com.cloudius.cli.util.Networking
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage

@Usage("manipulate the system ARP cache")
public class arp {
  @Usage("add ARP cache entry")
  @Command
  public void add(
    @Argument @Required @Usage("Interface name") String ifname,
    @Argument @Required @Usage("MAC address") String macaddr,
    @Argument @Required @Usage("IP address") String ip) {
    Networking.arp_add(ifname, macaddr, ip)
  }
}
