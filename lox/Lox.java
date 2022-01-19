import java.io.IOException;

public class Lox {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox {script}");
            System.exit(64);
        
        } else if (1 == args.length) {
            runFile(args[0]);

        } else {
            runPrompt();
        }
    }

    
}