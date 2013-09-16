package crash.commands.cloudius

import com.cloudius.cli.tests.TestRunner
import org.crsh.cli.completers.FileCompleter

// Path related
currentPath = new File('/')

getCurrentPath = { currentPath }
setCurrentPath = { currentPath = it }

// Test runner
testRunner = new TestRunner()
testRunnerRegistered = false

registerTests = { force = false ->
  if (!testRunnerRegistered || force) {
    testRunner.registerAllTests()
  }
}
registerTests()

welcome = { ->
  return """\
  ____   _____
 / __ \\ / ____|
| |  | | (_____   __
| |  | |\\___ \\ \\ / /
| |__| |____) \\ V /
 \\____/|_____/ \\_/

"""
}

prompt = { ->
  return "[${getCurrentPath().getPath()}]% "
}

