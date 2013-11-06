/*
 * Copyright (C) 2013 Sasha Levin
 *
 * This work is open source software, licensed under the terms of the
 * BSD license as described in the LICENSE file in the top-level directory.
 */

package crash.commands.cloudius

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.cli.Argument

class echo {
  @Usage("display a line of text")
  @Command
  Object main(@Argument String line) {
    out.print(line == null ? '' : line)
  }
}
