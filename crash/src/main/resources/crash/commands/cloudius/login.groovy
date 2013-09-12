package crash.commands.cloudius

currentPath = new File('/')

getCurrentPath = { currentPath }
setCurrentPath = { currentPath = it }

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

