package org.example.simplejava.converters;

import org.example.simplejava.helperObjects.CompilationResult;

public abstract class Converter <R, T> {
    CompilationResult result;
    public Converter(CompilationResult result) {
        this.result = result;
    }
    public abstract T convert(R input);
}
