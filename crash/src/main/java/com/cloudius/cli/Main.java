package com.cloudius.cli;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty("jline.terminal", System.getProperty("jline.terminal", OSvTerminal.class.getName()));
        org.crsh.cli.impl.bootstrap.Main.main(args);
    }
}
