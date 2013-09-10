package com.cloudius.cli;

import com.cloudius.util.Stty;
import jline.TerminalSupport;

/**
 * A minimal terminal implementation for JLine
 */
public class OSvTerminal extends TerminalSupport {
    static Stty stty = new Stty();

    public OSvTerminal() {
        // Call super() with supported = true
        super(true);

        setAnsiSupported(true);
    }

    @Override
    public void init() throws Exception {
        super.init();

        stty.jlineMode();
    }

    @Override
    public void restore() throws Exception {
        stty.reset();
        super.restore();

        // Newline in end of restore like in jline.UnixTermianl
        System.out.println();
    }
}
