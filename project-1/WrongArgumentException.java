///////////////////////////////////////////////////////////////////////////////
// 
// Main Class File:  P1.java
// File:             WrongArgumentException.java
// Semester:         CS536 Spring 2019
//
// Author:           Lingyun Guo (lguo57@wisc.edu)
// CS Login:         lingyun
// Lecturer's Name:  Loris D'Antoni
// Lab Section:      -
//
////////////////////////////////////////////////////////////////////////////////

/**
 * WrongArgumentException： inherit from Exception. If a wrong argument in a
 * (idName, Sym) pair is detected, WrongArgumentException will be raised
 *
 * Bugs: none known
 *
 * @author       Lingyun Guo
 *
 */

public class WrongArgumentException extends Exception {
    private String message;

    /**
     * constructor, set the message of the WrongArgumentException
     * @param message wrong argument information for this exception
     */
    WrongArgumentException(String message) {
        this.message =message;
    }

    /**
     * returns the wrong argument message of this exception
     * @return this.message: wrong argument information for this exception
     */
    @Override
    public String getMessage() {
        return this.message;
    }

}