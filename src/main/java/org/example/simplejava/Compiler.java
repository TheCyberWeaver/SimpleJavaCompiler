package org.example.simplejava;

import javafx.scene.paint.Color;
import org.example.simplejava.ASTTree.ASTProgram;
import org.example.simplejava.converters.*;
import org.example.simplejava.helperObjects.AssemblyCode;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;
import org.example.simplejava.tools.MsgLibrary;

import java.util.ArrayList;
import java.util.concurrent.*;

import static org.example.simplejava.helperObjects.MessageType.*;
import static org.example.simplejava.tools.MsgLibrary.*;

/**
 * {@code @Author:} Thomas Lu
 * The Main Compiler
 */

public class Compiler {

    public static final int RAM_SIZE = 1000;

    private final CompilationResult result = new CompilationResult();

    /**
     * {@code @Author:} Thomas Lu
     */
    public CompilationResult compile(String sourceCode, String targetLevel) {
        result.addMessage(MsgLibrary.get(WELCOME_MESSAGE), Color.WHITE);
        result.addMessage(INFO, MsgLibrary.get(COMPILATION_START));

        /*
           Step 1: Lexer
           - Skip spaces, newlines, and comments.
           - Identify tokens, such as int, x, 10.
         */
        Lexer lexer = new Lexer(result);
        ArrayList<Token> tokens = lexer.convert(sourceCode);
        if (targetLevel.equals("Tokens")) {
            prepareTokensOutput(tokens);
            return result;
        }

        /*
           Step 2: Preprocessor
           - simplify some part of the program
         */
        Preprocessor preprocessor = new Preprocessor(result);
        if (!preprocessor.checkGrammar(tokens)) {
            result.setOutputProgram("<Grammar Error>");
            result.addMessage(ERROR, "Grammar Error!!!");
            return result;
        }
        ArrayList<Token> preprocessedToken = preprocessor.convert(tokens);
        if (targetLevel.equals("Tokens(Processed)")) {
            prepareTokensOutput(preprocessedToken);
            return result;
        }

        /*
           Step 3: Parser
           - convert tokens to AST tree
         */
        Parser parser = new Parser(result);
        ASTProgram program = parser.convert(preprocessedToken);
        if (targetLevel.equals("AST Tree")) {
            result.setOutputProgram(program.toString());
            return result;
        }
        /*
           Step 4: Generator
           - convert AST tree to readable Assembly code.
         */
        Generator generator = new Generator(result);
        AssemblyCode asmCode = generator.convert(program);
        if (targetLevel.equals("Johnny Assembly")) {
            result.setOutputProgram(asmCode.toString());
            return result;
        }

        /*
           Step 5: Assembly to Johnny RAM code
           Tasks:
           - convert the generated assembly code to RAM code that can be recognized by the simulator Johnny
           - e.g. "take [x]" -> "01300"
         */
        Assembly2Johnny asm2Johnny = new Assembly2Johnny(result);
        String[] ram = asm2Johnny.convert(asmCode);
        if (targetLevel.equals("Johnny Code")) {
            prepareRAMCodeOutput(ram);
            return result;
        }

        return result;
    }

    private void prepareTokensOutput(ArrayList<Token> tokens) {
        StringBuilder tokensStr = new StringBuilder();
        for (Token token : tokens) {
            tokensStr.append(token.toString()).append("\n");
        }
        result.setOutputProgram(tokensStr.toString());
    }

    private void prepareRAMCodeOutput(String[] ram) {
        // Pack each line of the ram into the final output String
        StringBuilder ramFile = new StringBuilder();
        for (int i = 0; i < RAM_SIZE; i++) {
            ramFile.append(ram[i]).append("\n");
        }
        result.setOutputProgram(ramFile.toString());
    }

    /**
     * {@code @Author:} ChatGPT o1
     */
    public CompilationResult compileWithTimeout(String sourceCode, String targetLevel) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<CompilationResult> future = executor.submit(() -> compile(sourceCode, targetLevel));
        try {
            // Wait up to 2 seconds for the result
            return future.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Time Out
            future.cancel(true);

            CompilationResult failedResult = new CompilationResult();
            failedResult.addMessage(ERROR, "Compilation timed out");
            failedResult.setSuccess(false);

            return failedResult;
        } catch (Exception e) {
            CompilationResult failedResult = new CompilationResult();
            failedResult.addMessage(ERROR, "Compilation failed with exception");
            failedResult.setSuccess(false);

            e.printStackTrace();
            return failedResult;
        } finally {
            // Shutdown the executor to free resources
            executor.shutdownNow();
        }
    }
}
