package crash.commands.cloudius;

import com.cloudius.trace.Tracepoint;
import com.cloudius.trace.Callstack;
import com.cloudius.trace.Counter;
import org.crsh.cli.Argument;
import org.crsh.cli.Required;
import org.crsh.cli.Command;
import java.util.List;

public class perf {
    @Command
    public void list() {
        out.print("available tracepoints:\n\n");
        for (Tracepoint tp : Tracepoint.list()) {
            out.print(sprintf("    %s\n", tp.getName()));
        }
    }

    @Command
    public void callstack(@Required @Argument String name) {
        Tracepoint tp = new Tracepoint(name);
        Callstack[] traces = Callstack.collect(tp, 10, 20, 5000);
        if (traces.length == 0) {
            out.print(sprintf("no traces for %s\n", name));
        } else {
            out.print(sprintf("%10s  %s\n", "freq", "callstack"));
            for (Callstack trace : traces) {
                out.print(sprintf("%10d ", trace.getHits()));
                for (long pc : trace.getProgramCounters()) {
                    out.print(sprintf(" 0x%x", pc));
                }
                out.print('\n');
            }
        }
    }
}
