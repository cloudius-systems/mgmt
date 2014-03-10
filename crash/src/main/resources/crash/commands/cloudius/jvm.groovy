package crash.commands.cloudius

import com.cloudius.cli.command.FormatSizeOptions
import com.cloudius.cli.command.PropertyOptions
import com.cloudius.cli.util.SizeFormatter
import com.cloudius.cli.util.TextHelper
import org.crsh.cli.*
import org.crsh.command.InvocationContext
import org.crsh.command.PipeCommand
import org.crsh.text.ui.TableElement
import org.crsh.text.ui.UIBuilder
import org.crsh.util.Utils

import java.lang.management.ManagementFactory
import java.lang.management.MemoryUsage
import java.util.regex.Pattern

@Usage("JVM information")
class jvm {

  /**
   * Show JMX data about os.
   */
  @Usage("show JVM operating system")
  @Command
  public void system(InvocationContext<Map> context) {
    def os = ManagementFactory.operatingSystemMXBean;
    context.provide([name:"architecture",value:os?.arch]);
    context.provide([name:"name",value:os?.name]);
    context.provide([name:"version",value:os?.version]);
    context.provide([name:"processors",value:os?.availableProcessors]);
  }

  /**
   * Show JMX data about Runtime.
   */
  @Usage("show JVM runtime")
  @Command
  public void runtime() {
    def rt = ManagementFactory.runtimeMXBean
    context.provide([name:"name",value:rt?.name]);
    context.provide([name:"specName",value:rt?.specName]);
    context.provide([name:"specVendor",value:rt?.specVendor]);
    context.provide([name:"managementSpecVersion",value:rt?.managementSpecVersion]);
  }

  /**
   * Show JMX data about Class Loading System.
   */
  @Usage("show JVM classloding")
  @Command
  public void classloading() {
    def cl = ManagementFactory.classLoadingMXBean
    context.provide([name:"isVerbose ",value:cl?.isVerbose()]);
    context.provide([name:"loadedClassCount ",value:cl?.loadedClassCount]);
    context.provide([name:"totalLoadedClassCount ",value:cl?.totalLoadedClassCount]);
    context.provide([name:"unloadedClassCount ",value:cl?.unloadedClassCount]);
  }

  /**
   * Show JMX data about Compilation.
   */
  @Usage("show JVM compilation")
  @Command
  public void compilation() {
    def comp = ManagementFactory.compilationMXBean
    context.provide([name:"totalCompilationTime ",value:comp.totalCompilationTime]);
  }

  /**
   * Show memory heap.
   */
  @Usage("Show JVM memory heap")
  @Command
  public Object heap() {
    def heap = ManagementFactory.memoryMXBean.heapMemoryUsage
    return uiTableMemoryUsage(heap)
  }

  /**
   * Show memory non heap.
   */
  @Usage("Show JVM memory non heap")
  @Command
  public Object nonheap() {
    def nonHeap = ManagementFactory.memoryMXBean.nonHeapMemoryUsage
    return uiTableMemoryUsage(nonHeap)
  }

  /**
   * Show JMX data about Memory.
   */
  @Usage("List JVM memory pools")
  @Command
  public void pools() {
    ManagementFactory.memoryPoolMXBeans.each { pool ->
      context.println(pool.name);
    }
  }

  /**
   * Show JMX data about Memory.
   */
  @Usage("show memory pool details")
  @Command
  public Object pool(@Argument @Usage("the pool to show") List<String> pool) {
    def p = pool?.join(' ')
    def mem = ManagementFactory.memoryPoolMXBeans
    def found = mem.find { it.getName().equals(p) }

    if (found != null) {
      return uiTableMemoryUsage(found.usage)
    } else {
      return "memory pool '${p}' not found"
    }
  }

  private TableElement uiTableMemoryUsage(MemoryUsage usage) {
    def data = TextHelper.leftPadColumns([
      ["committed", "init", "max", "used"],
      [usage.committed as String,
        usage.init as String,
        usage.max as String,
        usage.used as String]
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

  /**
   * Top for JVM threads
   */
  @Usage("top for VM threads")
  @Command
  public void top(
    @Usage("Filter the threads with a glob expression on their name")
    @Option(names=["n","name"])
    String nameFilter,
    @Usage("Filter the threads with a glob expression on their group")
    @Option(names=["g","group"])
    String groupFilter,
    @Usage("Filter the threads by their status (new,runnable,blocked,waiting,timed_waiting,terminated)")
    @Option(names=["s","state"])
    String stateFilter) {
    def table = new UIBuilder().table(columns:[1]) {
      header(bold: true, fg: black, bg: white) {
        label("top");
      }
      row {
        eval {
          def args = [:];
          if (nameFilter != null) {
            args.name = nameFilter
          }
          if (stateFilter != null) {
            args.state = stateFilter;
          }
          if (groupFilter != null) {
            args.group = groupFilter;
          }
          // We need to use getProperty otherwise "thread" resolve to this class as a java.lang.Class object
          getProperty("jvm").ls args;
        }
      }
    }
    context.takeAlternateBuffer();
    try {
      while (!Thread.interrupted()) {
        out.cls()
        out.show(table);
        out.flush();
        Thread.sleep(1000);
      }
    }
    finally {
      context.releaseAlternateBuffer();
    }
  }

  private static final Pattern ANY = Pattern.compile(".*");

  /**
   * List the JVM threads, optionally filtered
   */
  @Usage("list vm threads")
  @Command
  public void ls(
    InvocationContext<Thread> context,
    @Usage("Filter the threads with a glob expression on their name")
    @Option(names=["n","name"])
    String nameFilter,
    @Usage("Filter the threads with a glob expression on their group")
    @Option(names=["g","group"])
    String groupFilter,
    @Usage("Filter the threads by their status (new,runnable,blocked,waiting,timed_waiting,terminated)")
    @Option(names=["s","state"])
    String stateFilter) {

    // Group filter
    Pattern groupPattern;
    if (groupFilter != null) {
      groupPattern = java.util.regex.Pattern.compile('^' + Utils.globexToRegex(groupFilter) + '$');
    } else {
      groupPattern = ANY;
    }

    // Name filter
    Pattern namePattern;
    if (nameFilter != null) {
      namePattern = java.util.regex.Pattern.compile('^' + Utils.globexToRegex(nameFilter) + '$');
    } else {
      namePattern = ANY;
    }

    // State filter
    Thread.State state = null;
    if (stateFilter != null) {
      try {
        state = Thread.State.valueOf(stateFilter.toUpperCase());
      } catch (IllegalArgumentException iae) {
        throw new ScriptException("Invalid state filter $stateFilter", iae);
      }
    }

    //
    Map<String, Thread> threads = getThreads();
    threads.each() {
      if (it != null) {
        def nameMatcher = it.value.name =~ namePattern;
        def groupMatcher = it.value.threadGroup.name =~ groupPattern;
        def thread = it.value;
        if (nameMatcher.matches() && groupMatcher.matches() && (state == null || it.value.state == state)) {
          try {
            context.provide(thread)
          }
          catch (IOException e) {
            e.printStackTrace()
          };
        }
      }
    }
  }

  static ThreadGroup getRoot() {
    ThreadGroup group = Thread.currentThread().threadGroup;
    ThreadGroup parent;
    while ((parent = group.parent) != null) {
      group = parent;
    }
    return group;
  }

  static Map<String, Thread> getThreads() {
    ThreadGroup root = getRoot();
    Thread[] threads = new Thread[root.activeCount()];
    while (root.enumerate(threads, true) == threads.length ) {
      threads = new Thread[threads.length * 2];
    }
    def map = [:];
    threads.each { thread ->
      if (thread != null)
        map["${thread.id}"] = thread
    }
    return map;
  }

  @Usage("interrupt VM threads")
  @Man("Interrupt VM threads")
  @Command
  public PipeCommand<Thread, Thread> interrupt(@Argument @Usage("the thread ids to interrupt") List<Thread> threads) {
    return new PipeCommand<Thread, Thread>() {
      void open() throws org.crsh.command.ScriptException {
        threads.each(this.&provide)
      }
      void provide(Thread element) throws IOException {
        element.interrupt();
        context.provide(element);
      }
    }
  }

  @Usage("stop vm threads")
  @Man("Stop VM threads.")
  @Command
  public PipeCommand<Thread, Thread> stop(@Argument @Usage("the thread ids to stop") List<Thread> threads) {
    return new PipeCommand<Thread, Thread>() {
      void open() throws org.crsh.command.ScriptException {
        threads.each(this.&provide)
      }
      void provide(Thread element) throws IOException {
        element.stop();
        context.provide(element);
      }
    }
  }

  @Usage("dump vm threads")
  @Man("Dump VM threads")
  @Command
  public PipeCommand<Thread, Object> dump(@Argument @Usage("the thread ids to dump") List<Thread> threads) {
    return new PipeCommand<Thread, Object>() {
      void open() throws org.crsh.command.ScriptException {
        threads.each(this.&provide)
      }
      void provide(Thread element) throws IOException {
        Exception e = new Exception("Thread ${element.id} stack trace")
        e.setStackTrace(element.stackTrace)
        e.printStackTrace(context.writer)
      }
    }
  }

  @Usage("list the vm system properties")
  @Command
  public void propls(
    InvocationContext<Map> context,
    @Usage("filter the property with a regular expression on their name")
    @Option(names=["f","filter"])
    String filter) {
    def pattern = java.util.regex.Pattern.compile(filter?:".*");
    System.getProperties().each { key, value ->
      def matcher = key =~ pattern;
      if (matcher.matches()) {
        try {
          context.provide([NAME: key, VALUE: value] as LinkedHashMap)
        }
        catch (IOException e) {
          e.printStackTrace()
        };
      }
    }
  }

  @Usage("set a system property")
  @Command
  public void propset(@PropertyOptions.PropertyName @Required String name, @PropertyOptions.PropertyValue @Required String value) {
    System.setProperty name.toString(), value
  }

  @Usage("get a system property")
  @Command
  public String propget(@PropertyOptions.PropertyName @Required String name) {
    return System.getProperty(name.toString()) ?: ""
  }

  @Usage("remove a system property")
  @Command
  public void proprm(@PropertyOptions.PropertyName @Required String name) {
    System.clearProperty name.toString()
  }

  /**
   * Show JMX data about Thread.
   */

  @Usage("Show JVM garbage collection information")
  @Command
  public void gcinfo() {

    out << "\nGARBAGE COLLECTION\n";
    ManagementFactory.garbageCollectorMXBeans.each { gc ->
      out << "name :" + gc?.name + "\n";
      context.provide([name:"collection count ",value:gc?.collectionCount]);
      context.provide([name:"collection time ",value:gc?.collectionTime]);


      String[] mpoolNames = gc.memoryPoolNames
      mpoolNames.each { mpoolName ->
        context.provide([name:"mpool name ",value:mpoolName]);
      }
      out << "\n\n";
    }
  }

  // Memory commands

  @Usage("Run the garbage collection")
  @Command
  public void gcrun() {
    System.gc()
  }

  @Usage("Display amount of free and used memory of the JVM")
  @Command
  public Object free(@FormatSizeOptions.B Boolean isB,
                     @FormatSizeOptions.Kb Boolean isKb,
                     @FormatSizeOptions.Mb Boolean isMb,
                     @FormatSizeOptions.Gb Boolean isGb,
                     @FormatSizeOptions.Human Boolean isH) {
    int total = Runtime.getRuntime().totalMemory()
    int free  = Runtime.getRuntime().freeMemory()

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
