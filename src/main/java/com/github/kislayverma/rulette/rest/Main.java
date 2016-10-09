package com.github.kislayverma.rulette.rest;

import org.restexpress.util.Environment;

public class Main {

    public static void main(String[] args) throws Exception {
        Configuration config = Environment.load(args, Configuration.class);
        config.initComponents();
        Server server = new Server(config);
        server.start().awaitShutdown();
    }
}