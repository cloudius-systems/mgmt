package crash.commands.cloudius

import com.cloudius.cli.tests.TestRunner

// Path related
currentPath = new File('/')

getCurrentPath = { currentPath }
setCurrentPath = { currentPath = it }
getResolvedPath = { it.startsWith('/') ? new File(it) : new File(getCurrentPath(), it) }

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

