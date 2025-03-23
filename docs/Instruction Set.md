## Extended Instruction Set

| Command    | Code Hi | Code Lo | Function                                                   |
|------------|---------|--------|------------------------------------------------------------|
| `00 val`   | `00`    | `val`  | The current memory cell contains a data value in the operand part. |
| `TAKE adr` | `01`    | `adr`  | Loads the operand part of the address `adr` into the accumulator. |
| `ADD adr`  | `02`    | `adr`  | Adds the operand part of the address `adr` to the accumulator. |
| `SUB adr`  | `03`    | `adr`  | Subtracts the operand part of the address `adr` from the accumulator. |
| `SAVE adr` | `04`    | `adr`  | Stores the content from the accumulator into the operand part of the address `adr`. |
| `JMP adr`  | `05`    | `adr`  | Jumps to the address `adr`.                                |
| `TST adr`  | `06`    | `adr`  | Compares the operand part of the address `adr` with zero. If equal, the next command is skipped. |
| `INC adr`  | `07`    | `adr`  | Increases the operand part of the address `adr` by one using the accumulator. |
| `DEC adr`  | `08`    | `adr`  | Decreases the operand part of the address `adr` by one using the accumulator. |
| `NULL adr` | `09`    | `adr`  | Stores the data value zero in the operand part of the address `adr`. |
| `HLT`      | `10`    |        | Ends the program.                                           |
| `SAVI adr` | `11`    | `adr`  | Enhances the original save instruction by saving a register's value into the memory address pointed to by addr.      |
| `JMPI adr` | `12`    | `adr`  | Enhances the original jmp instruction by jumping to the memory address pointed to by addr.     |
