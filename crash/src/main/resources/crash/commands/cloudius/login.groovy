package crash.commands.cloudius

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

