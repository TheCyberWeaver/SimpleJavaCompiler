

package org.example.simplejava.converters;

import javafx.scene.paint.Color;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;

import java.util.ArrayList;

import static org.example.simplejava.helperObjects.MessageType.*;

/**
 * {@code @Author:} Thomas Lu
 * The Preprocessor
 */

public class Preprocessor extends Converter<ArrayList<Token>, ArrayList<Token>> {

    public Preprocessor(CompilationResult result) {
        super(result);
    }

    @Override
    public ArrayList<Token> convert(ArrayList<Token> input) {
        result.addMessage("<======================== Preprocessor Start ========================>", Color.LIGHTSALMON);

        ArrayList<Token> finalTokens=new ArrayList<>();

        if(!input.isEmpty()){
            // Step 1: Remove the class definition (if present) along with its braces.
            ArrayList<Token> tokensAfterClassRemoval = removeClassWrapper(input);
            result.addMessage(INFO,"Removed class definition");

            // Step 2: Merge the main method tokens into a single token.
            finalTokens = mergeMainFunctionTokens(tokensAfterClassRemoval);
            result.addMessage(INFO,"Simplified Main Function definition");
        }

        for (Token token : finalTokens) {
            result.addMessage(DEBUG,token+"\n");
        }
        result.addMessage(INFO,"Total number of tokens: "+finalTokens.size());
        result.addMessage("<======================== Preprocessor End ========================>", Color.LIGHTSALMON);
        return finalTokens;
    }
    public boolean checkGrammar(ArrayList<Token> tokens) {
        //TODO
        return true;
    }
    /**
     * {@code @Author:} GPT o3-mini-high
     * Removes the class wrapper from the beginning of the program if it exists.
     */
    private ArrayList<Token> removeClassWrapper(ArrayList<Token> tokens) {
        ArrayList<Token> processedTokens = new ArrayList<>();

        // Assume the class definition starts with a token with value "class"
        if (!tokens.isEmpty() && tokens.getFirst().value().equals("class")) {
            // Find the first LEFT_BRACKET token "{"
            int index = 0;
            while (index < tokens.size() && tokens.get(index).type() != Token.Type.LEFT_BRACKET) {
                index++;
            }
            if (index < tokens.size() && tokens.get(index).type() == Token.Type.LEFT_BRACKET) {
                // Skip the LEFT_BRACKET
                index++;
                int braceCount = 1;
                int start = index;
                // Find the matching RIGHT_BRACKET
                while (index < tokens.size() && braceCount > 0) {
                    Token t = tokens.get(index);
                    if (t.type() == Token.Type.LEFT_BRACKET) {
                        braceCount++;
                    } else if (t.type() == Token.Type.RIGHT_BRACKET) {
                        braceCount--;
                        if (braceCount == 0) {
                            // Found the matching RIGHT_BRACKET; do not include it
                            break;
                        }
                    }
                    index++;
                }
                // Add tokens between start and index (the content inside the class) to the result
                processedTokens.addAll(tokens.subList(start, index));
            } else {
                // If no LEFT_BRACKET is found, return the original tokens
                processedTokens = tokens;
            }
        } else {
            processedTokens = tokens;
        }
        return processedTokens;
    }

    /**
     * {@code @Author:} GPT o3-mini-high
     * Searches for and merges the following sequence of tokens:
     * [VARIABLE]: static
     * [VARIABLE]: void
     * [MAIN_FUNCTION]: main
     * [LEFT_PAREN]: (
     * [VARIABLE]: String
     * [VARIABLE]: args
     * [RIGHT_PAREN]: )
     * and replaces them with a single token: [MAIN_FUNCTION]: main.
     */
    private ArrayList<Token> mergeMainFunctionTokens(ArrayList<Token> tokens) {
        ArrayList<Token> processedTokens = new ArrayList<>();
        int i = 0;
        while (i < tokens.size()) {
            if (i + 6 < tokens.size() &&
                    tokens.get(i).type() == Token.Type.VARIABLE && tokens.get(i).value().equals("static") &&
                    tokens.get(i + 1).type() == Token.Type.DATATYPE && tokens.get(i + 1).value().equals("void") &&
                    tokens.get(i + 2).type() == Token.Type.MAIN_FUNCTION && tokens.get(i + 2).value().equals("main") &&
                    tokens.get(i + 3).type() == Token.Type.LEFT_PAREN && tokens.get(i + 3).value().equals("(") &&
                    tokens.get(i + 4).type() == Token.Type.VARIABLE && tokens.get(i + 4).value().equals("String") &&
                    tokens.get(i + 5).type() == Token.Type.VARIABLE && tokens.get(i + 5).value().equals("args") &&
                    tokens.get(i + 6).type() == Token.Type.RIGHT_PAREN && tokens.get(i + 6).value().equals(")")
            ) {
                // Found the sequence; add a new MAIN_FUNCTION token
                processedTokens.add(new Token(Token.Type.MAIN_FUNCTION, "main"));
                i += 7; // Skip the next 7 tokens
            } else {
                processedTokens.add(tokens.get(i));
                i++;
            }
        }
        return processedTokens;
    }
}


