package crash.commands.cloudius

import com.cloudius.net.IFConfig
import com.cloudius.util.ELFLoader
import org.crsh.cli.Argument
import org.crsh.cli.Command

class ifconfig  {
  @Command
  void main(@Argument String ifname, @Argument String ip, @Argument String netmask) {
    if (ifname == null) {
      argv = ["/tools/ifconfig.so"]
      new ELFLoader().run(argv as String[])
    } else if (ifname != null && ip != null && netmask != null) {
      if (!IFConfig.set_ip(ifname, ip, netmask)) {
        throw new ScriptException("failed to set ip")
      }

      if (!IFConfig.if_up(ifname)) {
        throw new ScriptException("unable to if_up interface")
      }
    }

  }
}
