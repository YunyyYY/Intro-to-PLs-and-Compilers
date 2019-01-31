///////////////////////////////////////////////////////////////////////////////
//
// Main Class File:  P1.java
// File:             SymTable.java
// Semester:         CS536 Spring 2019
//
// Author:           Lingyun Guo (lguo57@wisc.edu)
// CS Login:         lingyun
// Lecturer's Name:  Loris D'Antoni
// Lab Section:      -
//
////////////////////////////////////////////////////////////////////////////////

import java.util.*;

/**
 * SymTable： stores the name strings and their corresponding symbols
 * in each scope
 *
 * Bugs: none known
 *
 * @author       Lingyun Guo
 *
 */

public class SymTable {
    private LinkedList<HashMap<String, Sym>> table;  // stores List of HashMaps

    /**
     * constructor, initialize list field to contain a single, empty HashMap.
     */
    public SymTable() {
        table = new LinkedList<HashMap<String, Sym>>();
        table.addFirst(new HashMap<String, Sym>());
    }

    /**
     * add (idName, sym) to the first scope
     * @param idName a string represents the name of the Sym.
     * @param sym a Sym object mapped by its name
     */
    public void addDecl(String idName, Sym sym) throws
            EmptySymTableException, WrongArgumentException,
            DuplicateSymException
    {
        if (table.isEmpty())
            throw new EmptySymTableException();
        if ((idName == null) && (sym == null))
            throw new WrongArgumentException("Id name and sym are null");
        else if (idName == null)
            throw new WrongArgumentException("Id name is null");
        else if (sym == null)
            throw new WrongArgumentException("Sym is null");
        if (table.getFirst().containsKey(idName))
            throw new DuplicateSymException();
        table.getFirst().put(idName, sym);
    }

    /**
     * add a new scope to the front of the List.
     */
    public void addScope() {
        table.addFirst(new HashMap<String, Sym>());
    }

    /**
     * this function looks up a given idName in the local scope
     * if no target is found, return null
     * @param idName name of the Sym to be looked for
     */
    public Sym lookupLocal(String idName) throws EmptySymTableException {
        if (table.isEmpty())
            throw new EmptySymTableException();
        return table.getFirst().get(idName);
    }

    /**
     * this function looks up a given idName in all scopes and
     * returns on first match, otherwise, return null
     * @param idName name of the Sym to be looked for
     */
    public Sym lookupGlobal (String idName) throws EmptySymTableException {
        if (table.isEmpty())
            throw new EmptySymTableException();
        Sym result;
        for (HashMap<String, Sym> temp: table) {
            result = temp.get(idName);
            if (result != null)
                return result;
        }
        return null;
    }

    /**
     * this function removes the front scope of the List.
     */
    public void removeScope() throws EmptySymTableException {
        if (table.isEmpty())
            throw new EmptySymTableException();
        table.removeFirst();
    }

    /**
     * this function print out the current layout of SymTable.
     */
    public void print() {
        System.out.println("\n=== Sym Table ===\n");
        for (HashMap<String, Sym> temp: table) {
            System.out.println(temp.toString());
        }
        System.out.println("\n");
    }
}
