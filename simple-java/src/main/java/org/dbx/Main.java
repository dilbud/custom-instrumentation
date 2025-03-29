package org.dbx;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class.getName());

//    private static Instrumentation instrumentation;
//
//    public static void premain(String agentArgs, Instrumentation inst) {
//        instrumentation = inst;
//    }
//
//    public static void listMethods() {
//        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
//            if (clazz.getClassLoader() != null) { // Skip system classes
//                System.out.println("Class: " + clazz.getName());
//                for (Method method : clazz.getDeclaredMethods()) {
//                    System.out.println("  Method: " + method.getName());
//                }
//            }
//        }
//    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please enter your sentence:");
            String input = scanner.nextLine();
            if("exit".equals(input)) {
                break;
            }
            Main main = new Main();
            main.call1();
            int wordCount = main.countWords(input);
            main.call2();
            System.out.println("The input contains " + wordCount + " word(s).");
        }
    }

    public void call1() {
        System.out.println("Entering method: " + "call1");
    }

    public void call2() {
        System.out.println("Entering method: " + "call2");
    }

    public int countWords(String input) {

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (input == null || input.isEmpty()) {
            return 0;
        }

        String[] words = input.split("\\s+");
        return words.length;
    }
}
