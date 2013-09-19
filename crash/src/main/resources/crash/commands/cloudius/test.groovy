package crash.commands.cloudius

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion

class test implements Completer {
  @Usage("List available tests")
  @Command
  String list() {
    testRunner.getTestNames().join('\n')
  }

  @Usage("Invoke a test")
  @Command
  void invoke(
    @Argument(completer = test.class) @Required String name
  ) {
    if (!testRunner.getTestNames().find({ it == name })) {
      throw new ScriptException("test not found")
    }

    out.println(">>> Running test ${name}...")
    out.flush()
    beginNanoTime = System.nanoTime()
    result = testRunner.run(name)

    durationNanoTime = (System.nanoTime() - beginNanoTime)
    durationTime = durationNanoTime / 1000000000.0

    out.println(String.format(">>> Test duration %.6f s, %d ns\n", durationTime, durationNanoTime))
    out.println(">>> Test completed " + (result ? "successfully!" : "unsuccessfully..."))
  }

  @Override
  Completion complete(ParameterDescriptor parameter, String prefix) throws Exception {
    Completion.Builder builder = Completion.builder(prefix)
    testRunner.getTestNames().each {
      if (it.startsWith(prefix)) {
        builder.add(it.substring(prefix.length()), true)
      }
    }
    builder.build()
  }
}
