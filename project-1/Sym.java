///////////////////////////////////////////////////////////////////////////////
//
// Main Class File:  P1.java
// File:             Sym.java
// Semester:         CS536 Spring 2019
//
// Author:           Lingyun Guo (lguo57@wisc.edu)
// CS Login:         lingyun
// Lecturer's Name:  Loris D'Antoni
// Lab Section:      -
//
////////////////////////////////////////////////////////////////////////////////

/**
 * Sym: represents a symbol and provides a series of methods as interface.
 *
 * Bugs: none known
 *
 * @author       Lingyun Guo
 *
 */

public class Sym {
    String type;  // stores type of the symbol

    /**
     * constructor, initialize a symbol with its type
     * @param type specifies type of the symbol
     */
    public Sym(String type) {
        this.type = type;
    }

    /**
     * get the type of this symbol
     */
    public String getType() {
        return type;
    }

    /**
     * currently the same as getType(), get the type of this symbol
     */
    @Override
    public String toString() {
        return type;
    }

}
