package org.example.simplejava.tools;

import java.util.HashMap;
import java.util.Map;

import static org.example.simplejava.SimpleJavaUI.LOGGING_STYLE;

/**
 * {@code @Author:} Thomas Lu
 */

public class MsgLibrary {

    private static final Map<String, String[]> messages = new HashMap<>();

    public static final String MESSAGE_NOT_FOUND = "MESSAGE_NOT_FOUND";
    public static final String WELCOME_MESSAGE = "WELCOME_MESSAGE";
    public static final String CRITICAL_ERROR = "EXAMPLE_ERROR_MESSAGE";

    public static final String COMPILATION_START = "COMPILATION_START";
    public static final String COMPILATION_END = "COMPILATION_END";

    //Lexer
    public static final String MAIN_FUNCTION_DETECTED = "MAIN_FUNCTION_DETECTED";
    public static final String PRIVATE_MODIFIER_DETECTED = "PRIVATE_MODIFIER_DETECTED";
    public static final String SYSTEM_OUTPUT_DETECTED = "SYSTEM_OUTPUT_DETECTED";

    public static final String UNEXPECTED_CHARACTER = "UNEXPECTED_CHARACTER";

    //Parser
    public static final String PARSING_FUNCTION = "PARSING_FUNCTION";
    public static final String PARSING_FUNCTION_CALL = "PARSING_FUNCTION_CALL";
    public static final String FUNCTION_NOT_DEFINED = "FUNCTION_NOT_DEFINED";
    public static final String STATEMENT_NOT_KNOWN = "STATEMENT_NOT_KNOWN";
    public static final String ASSIGNING_VAR = "ASSIGNING_VAR";
    public static final String ASSIGNING_VAR_MONO_OPERATOR = "ASSIGNING_VAR_MONO_OPERATOR";
    public static final String VAR_ADDED = "VAR_ADDED";
    public static final String VAR_ADDED_WITH_INIT = "VAR_ADDED_WITH_INIT";
    public static final String TOKEN_UNEXPECTED_DURING_ADVANCE = "TOKEN_UNEXPECTED_DURING_ADVANCE";

    //Generator
    public static final String ASSIGN_MEM_TO_VAR = "ASSIGN_MEM_TO_VAR";
    public static final String ASSIGN_MEM_TO_PAR = "ASSIGN_MEM_TO_PAR";

    static {
        messages.put(MESSAGE_NOT_FOUND, new String[]{
                "Message not found.\n",
                "Ara ara~ (✿´ ꒳ ` )♡ It seems the message has run away... Fufu~ Did you scare it? (˘▾˘~) Or maybe... it's hiding in your heart all along? (♡ >ω< ♡)\n",
        });
        
        messages.put(WELCOME_MESSAGE, new String[]{
                "--------------------------------------------------------------------------------------------\n"+
                "Welcome to Simple Java Compiler!\n"+
                "--------------------------------------------------------------------------------------------\n",

                "--------------------------------------------------------------------------------------------\n"+
                "Ehehe~ ヾ(≧▽≦*)o Hello, hello, user-chamaaa! (✿◕‿◕) Did you miss me? (⁄ ⁄>⁄ω⁄<⁄ ⁄) Hehe~ Let’s have lots of fun together, okay? (´｡• ᵕ •｡`) ♡\n"+
                "--------------------------------------------------------------------------------------------\n",
        });
        messages.put(CRITICAL_ERROR, new String[]{
                "Critical error encountered.\n",
                "E-ehhh?! (⊙﹏⊙✿) C-Critical error...?! (grabs your sleeve) N-no way... D-Does this mean… I’m dying?! (；ω；) H-help me, user-samaaa~! (＞﹏＜) \n"
        });

        messages.put(COMPILATION_START, new String[]{
                "Compilation started...\n",
                "Uwahh~? Compilation-chan is starting? (✿◕‿◕) Ganbatte ne~ (っ˘з(˘⌣˘ ) \uD83D\uDC95 \n"
        });
        messages.put(COMPILATION_END, new String[]{
                "Compilation Succeed.\n",
                "Sugoii~! As expected of my senpai~! (´﹃｀) Nnn~ seeing you succeed makes my kokoro go doki doki~ \uD83D\uDC95 \n"
        });

        //Lexer
        messages.put(MAIN_FUNCTION_DETECTED, new String[]{
                "Main function detected. Marked \n",
                "Mmm~ The main() method, so crucial, so central... \n",
        });
        messages.put(PRIVATE_MODIFIER_DETECTED, new String[]{
                "detected private modifier, will be treated as public\n",
                "Ufufu~ A little privacy? Denied~ Now it’s all public for everyone to see... embarrassing, ne~? (⁄ ⁄•⁄ω⁄•⁄ ⁄)♡\n",
        });
        messages.put(SYSTEM_OUTPUT_DETECTED, new String[]{
                "detected System.out.println method, simplifying it to println\n",
                "Nyaa~ I'm gonna strip away the System.out and leaving just println... Ahh, so much cleaner... so much more direct~ (๑˃ᴗ˂)ﻭ♡\n",
        });

        messages.put(UNEXPECTED_CHARACTER, new String[]{
                "Unexpected character:[%s]\n",
                "Ara~? This little one wasn’t supposed to be here… -> %s\n",
        });

        //Parser

        messages.put(PARSING_FUNCTION, new String[]{
                "Parsing function: %s\n",
                "I peeked into this every little function %s, and analysed it so closely…\n",
        });
        messages.put(PARSING_FUNCTION_CALL, new String[]{
                "Parsing function call: %s\n",
                "Mmm~(๑˃́ꇴ˂̀๑)\uD83D\uDC97 function call %s, carefully analyzing...♡\n",
        });
        messages.put(FUNCTION_NOT_DEFINED, new String[]{
                "Function not defined: %s\n",
                "Mmm~ Trying to call something undefined? Ahh, such a bad boy… (¬‿¬ ) Function %s is not gonna answer your call \n",
        });
        messages.put(STATEMENT_NOT_KNOWN, new String[]{
                "Statement not known (start with token): %s\n",
                "Ehh~?(´｡• ᵕ •｡`) ♡ I don’t know what to do with this… it’s so foreign, so confusing… It starts with token: %s\n",
        });
        messages.put(ASSIGNING_VAR, new String[]{
                "Assigning variable: %s\n",
                "Carefully placing a value to here (｡>﹏<｡)♡: %s\n",
        });
        messages.put(ASSIGNING_VAR_MONO_OPERATOR, new String[]{
                "Assigning variable (MonoOperator Detected): %s\n",
                "Carefully placing a value to here \uD83D\uDC96(MonoOperator Detected): %s\n",
        });
        messages.put(VAR_ADDED, new String[]{
                "Variable added: %s\n",
                "Nyaa~ (๑˃́ꇴ˂̀๑)\uD83D\uDC97? A fresh, new variable…: %s\n",
        });
        messages.put(VAR_ADDED_WITH_INIT, new String[]{
                "Variable added: %s initialized by: %s\n",
                "Nyaa~ (⁄ ⁄•⁄ω⁄•⁄ ⁄)\uD83D\uDCA6? A fresh, new variable…: %s ,and I'll just give it the value %s!\n",
        });
        messages.put(TOKEN_UNEXPECTED_DURING_ADVANCE,new String[]{
                "Required Type:[%s] Detected Token: %s\n",
                "Nyaa~? I was expecting a %s ... but you gave me this %s instead~? Ahh, User-chan, you’re so unpredictable~ (✧ω✧)\n",
        });

        //Generator
        messages.put(ASSIGN_MEM_TO_VAR,new String[]{
                "Variable %s has been assigned to %d\n",
                "I'm giving this variable-chan\uD83D\uDC96 %s a super special home♡ at address %d (´｡• ᵕ •｡`) ♡\n",
        });
        messages.put(ASSIGN_MEM_TO_PAR,new String[]{
                "Parameter %s has been assigned to %d\n",
                "I'm giving this parameter-chan\uD83D\uDC96 %s a super special home♡ at address %d (*≧ω≦)\n",
        });

    }
    public static String get(String messageId, Object... args) {
        if (messages.containsKey(messageId)) {
            String message = messages.get(messageId)[LOGGING_STYLE];
            if (args.length > 0) {
                message = String.format(message, (Object[]) args);
            }
            return message;
        }
        String notFoundMessage = messages.get(MESSAGE_NOT_FOUND)[LOGGING_STYLE];
        return String.format(notFoundMessage, (Object[]) args);
    }
}
