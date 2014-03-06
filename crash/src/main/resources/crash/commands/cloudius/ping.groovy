package crash.commands.cloudius

import org.crsh.cli.Argument
import org.crsh.cli.Command
import java.net.InetAddress

class ping {
  @Usage("send ICMP ECHO_REQUEST to network hosts")
  @Command
  void main(@Argument @Required @Usage("IP address") String addr) {
    def timeout = 2000;
    def nping = 10;
    def inetAddr = InetAddress.getByName(addr);
    out.println("Pinging to " + addr + ":");
    for (int i = 0; i < nping; i++) {
        def start = System.currentTimeMillis();
        def reachable = inetAddr.isReachable(timeout);
        def end = System.currentTimeMillis();
        def time = end - start;
        if (reachable) {
            if (time > 0)
                out.println("Reply from " + addr + ": time=" + time + " ms");
            else
                out.println("Reply from " + addr + ": time=<1 ms");
        } else {
                out.println("Request timed out.");
        }
        out.flush();
        Thread.sleep(1000);
    }
  }
}
