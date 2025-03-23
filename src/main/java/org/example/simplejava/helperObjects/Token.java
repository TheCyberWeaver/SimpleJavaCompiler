package org.example.simplejava.helperObjects;

/**
 * {@code @Author:} Thomas Lu
 */

public record Token(Type type, String value) {

    public String toString() {
        return "[" + type + "]: " + value;
    }

    public enum Type {
        DATATYPE,       // keyword int
        VARIABLE,       // variable
        STATEMENT_KEYWORD,
        ASSIGN,         //  =
        INT_LITERAL,    // integer (constant number)
        OPERATOR,       //  +,-
        MONO_OPERATOR,  // +=,-=,++,--
        SEMICOLON,      //  ;
        COMMA,
        LEFT_BRACKET,   // {
        RIGHT_BRACKET,  // }
        LEFT_PAREN,     // (
        RIGHT_PAREN,    // )
        MAIN_FUNCTION,
    }
}
