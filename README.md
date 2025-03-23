# Simple Java Compiler

Simple Java is a lightweight compiler that translates a subset of Java into johnny assembly code. It’s designed as a learning tool to help bridge the gap between high-level programming and low-level machine operations.

## Key Features

- **Core Functionality:**  
  Compiles simple Java programs with basic variable declarations, assignments, conditionals, while loops, and static function calls.

- **Extended Instructions:**  
  Includes `savi addr` and `jmpi addr` for enhanced memory operations.

- **Detailed Logs:**  
  Generates comprehensive logs during each compilation stage.

## Limitations

- Only supports the `int` data type.
- Does not support negative numbers, decimals, or advanced data types.
- Lacks support for recursion and nested function calls.
- Basic arithmetic (only addition and subtraction) and comparison operators are supported.

## Quick Start

- **Run the Compiler:**  
  Double-click **SimpleJava-1.2.jar** to launch the compiler.

- **GUI Tool:**  
  Use the additional jar file for quickly editing johnny memory files and viewing program outputs.

- **Source & Documentation:**
    - **Source Code:** Located in `\src\main\java\org\example\simplejava`
      - **Documentation:** Found in the `\docs` folder

## Recommendations

- **IDE:**  
  IntelliJ IDEA or VSCode is recommended for working with the source code.

- **JRE:**  
  Use Java 21 or higher (tested), though it should work with Java 16 as well.

Happy coding and enjoy exploring the compiler!
