package crash.commands.cloudius

import com.cloudius.cli.command.OSvCommand
import org.crsh.cli.Command
import org.crsh.cli.Usage
import com.cloudius.cli.util.OSvAPI

class dmesg {
    @Usage("Output debug messages")
    @Command
    Object main() {
        return OSvAPI.get("/os/dmesg")
    }
}
