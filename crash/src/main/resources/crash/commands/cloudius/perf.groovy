package crash.commands.cloudius;

import com.cloudius.trace.Tracepoint;
import com.cloudius.trace.Callstack;
import com.cloudius.trace.Counter;
import org.crsh.cli.Argument;
import org.crsh.cli.Required;
import org.crsh.cli.Command
import org.crsh.cli.Usage;

import java.util.List;
import java.lang.Thread;
import java.lang.String;

@Usage("view tracepoint callstacks")
public class perf {
    @Command
    @Usage("list available tracepoints")
    public void list() {
        out.print("available tracepoints:\n\n");
        for (Tracepoint tp : Tracepoint.list()) {
            out.print(sprintf("    %s\n", tp.getName()));
        }
    }

    @Command
    @Usage("print the callstack of a spcific tracepoint")
    public void callstack(@Required @Usage("name of tracepoint") @Argument String name) {
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

    @Command
    public void stat(@Required @Argument String tracepoints) {
        String[] names = tracepoints.split(',');
	Tracepoint[] tps = new Tracepoint[names.length];
	Counter[] counts = new Counter[names.length];
	long[] lasts = new long[names.length];
	int[] lens = new int[names.length];
	for (int i = 0; i < names.length; i++) {
	    tps[i] = new Tracepoint(names[i]);
	    counts[i] = new Counter(tps[i]);
	    lasts[i] = 0;
	    lens[i] = names[i].length()+1;
	    if(lens[i] < 7) {
	         lens[i] = 7;
	    }
	}
	int line = 0;
	try {
	while (true) {
            if (line++ % 23 == 0) {
	        for (int i = 0; i < names.length; i++) {
	            out.print(sprintf("%"+lens[i]+"s", tps[i].getName()));
		}
		out.print("\n");
	    }
	    for (int i = 0; i < names.length; i++) {
	        long now = counts[i].read();
	        out.print(sprintf("%"+lens[i]+"d", now - lasts[i]));
	        lasts[i] = now;
	    }
	    out.print("\n");
	    out.flush();
	    Thread.sleep(1000);
	}
	} finally {
	    for (int i = 0; i < names.length; i++) {
	        counts[i].close();
	    }
	}
    }
}
