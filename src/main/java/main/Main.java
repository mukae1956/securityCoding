package main;

import controller.Controller;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        Controller c = new Controller();
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        c.run();
    }
}
