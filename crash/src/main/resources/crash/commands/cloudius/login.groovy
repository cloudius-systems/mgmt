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

welcome = { "" }
prompt = { ->
  return "[${getCurrentPath().getPath()}]% "
}

