///////////////////////////////////////////////////////////////////////////////
//                   
// Title:            Programming Assignment 1
// Files:            P1.java
// Semester:         CS536 Spring 2019
//
// Author:           Lingyun Guo
// Email:            lguo57@wisc.edu
// CS Login:         lingyun
// Lecturer's Name:  Loris D'Antoni
// Lab Section:      -
//
///////////////////////////////////////////////////////////////////////////////

/**
 * P1
 *
 * This class tests all of the Sym and SymTable operations and all
 * situations under which exceptions are thrown.
 * It also consider 'boundary' and 'non-boundary' cases.
 *
 * Operations of Sym being tested:
 *       Sym(String type)    -- constructor
 *       String getType()    -- return this Sym's type
 *       String toString()   -- return this Sym's type
 *
 * Operations of SymTable being tested:
 *       SymTable()          -- constructor
 *       void addDecl(String -- throw exceptions:
 *       idName, Sym sym)       SymTable empty: EmptySymTableException
 *                              null para: WrongArgumentException
 *                              hash has id: DuplicateSymException
 *                              otherwise add to the *first* HashMap.
 *       void addScope()     -- add a new empty HashMap to the front
 *                              of list.
 *       Sym lookupLocal(    -- SymTable empty: EmptySymTableException
 *       String idName)         o/w, if 1st HashMap contains, return 
 *                              o/w, return null.
 *       Sym lookupGlobal(   -- SymTable empty: EmptySymTableException
 *       String idName)         o/w, if *ANY* HashMap contains, return
 *                              Sym. o/w, return null.
 *       void removeScope()  -- SymTable empty: EmptySymTableException
 *                              o/w remove HashMap from front of list
 *       void print()        -- debug method
 *
 * This code tests every List operation, including both correct and
 * bad calls to the operation (remove) that can throw an exception.
 *
 * It produces output ONLY if a test fails, except for the print() method
 * of the SymTable class.
 */

public class P1 {
    /** The main function tests all the operations of Sym and SymTable */
    public static void main(String[] args) {
        Sym sym1 = new Sym("a");


        // test getType() and toString() for sym, currently both
        // functions have the same code and are identical
        if (sym1.getType() == null) {
            System.out.println("[Sym] return type of Sym is null");
        }

        SymTable symTable = new SymTable();
        try {
            System.out.println("[0] <initialized case> layout of SymTable:");
            symTable.print();
        } catch (Exception ex) {
            System.out.println("[print] unexpected exception when" +
                    " printing initialized case");
        }

        // test if SymTable is successfully initialized with addDecl()
        // in this case, only EmptySymTableException can be thrown
        // if other Exception is thrown, addDecl() fails
        try{
            symTable.addDecl("id1", sym1);
        }
        catch (EmptySymTableException ex) {
            System.out.println("[SymTable] fail to initialize a SymTable");
        }
        catch (DuplicateSymException ex) {
            System.out.println("[addDecl] unexpected DuplicateSymException");
        }
        catch (WrongArgumentException ex) {
            System.out.println("[addDecl] unexpected WrongArgumentException");
        }

        // if no unexpected exception thrown and current layout correct,
        // normal case of addDecl() passes all exception cases and successes.
        try {
            System.out.println("[1] <one element case> layout of SymTable:");
            symTable.print();
        } catch (Exception ex) {
            System.out.println("[print] unexpected exception when" +
                    " printing one element case");
        }

        // further test three types of exception cases of addDecl()
        // WrongArgumentException null idName case
        try {
            symTable.addDecl(null, sym1);
            System.out.println("[addDecl] no WrongArgumentException throws" +
                    "with null idName");
        }
        catch (Exception ex) {
            if (!ex.getMessage().equals("Id name is null")) {
                System.out.println("[addDecl] throws wrong type of " +
                        "WrongArgumentException");
            }
        }

        // WrongArgumentException null idName case
        try {
            symTable.addDecl("id1", null);
            System.out.println("[addDecl] no WrongArgumentException throws" +
                    "with null sym");
        }
        catch (Exception ex) {
            if (!ex.getMessage().equals("Sym is null")) {
                System.out.println("[addDecl] throws wrong type of " +
                        "WrongArgumentException");
            }
        }

        // WrongArgumentException null idName and null Sym case
        try {
            symTable.addDecl(null, null);
            System.out.println("[addDecl] no WrongArgumentException throws" +
                    "with null idName and Sym");
        }
        catch (Exception ex) {
            if (!ex.getMessage().equals("Id name and sym are null")) {
                System.out.println("[addDecl] throws wrong type of " +
                        "WrongArgumentException");
            }
        }

        // DuplicateSymException case
        try {
            symTable.addDecl("id1", sym1);
            System.out.println("[addDecl] no DuplicateSymException throws");
        }
        catch (Exception ex) {  // DuplicateSymException caught
        }

        // clear the list with removeScope(), normal case
        try{
            symTable.removeScope();
        } catch (EmptySymTableException ex) {
            System.out.println("[removeScope] unexpected exception");
        }

        // if layout [2] is the same as [1], removeScope() fails
        try {
            System.out.println("[2] <empty scope case> layout of SymTable:");
            symTable.print();
        } catch (Exception ex) {
            System.out.println("[print] unexpected exception when" +
                    " printing empty scope case");
        }

        // test EmptySymTableException cases for each operation
        try {
            symTable.addDecl("id1", sym1);
            System.out.println("[addDecl] no EmptySymTableException throws");
        } catch (Exception ex) { // caught empty SymTable exception
        }

        try{
            symTable.removeScope();
            System.out.println("[removeScope] no EmptySymTableException" +
                    " throws");
        } catch (Exception ex) {
        }

        try {
            symTable.lookupLocal("id1");
            System.out.println("[lookupLocal] no EmptySymTableException" +
                    " throws");
        } catch (Exception ex) { // caught empty SymTable exception
        }

        try {
            symTable.lookupGlobal("id1");
            System.out.println("[lookupGlobal] no EmptySymTableException" +
                    " throws");
        } catch (Exception ex) { // caught empty SymTable exception
        }

        // test addScope()
        try{
            symTable.addScope();
            symTable.removeScope();
        } catch (EmptySymTableException ex) {
            System.out.println("[addScope] fail to create a new HashMap");
        }

        // test if removeScope() has successfully removed the HashMap
        try{
            symTable.removeScope();
            System.out.println("[removeScope] fail to remove scope");
        } catch (Exception ex) { // removed, finish test removeScope()
        }

        // test proper case for lookupLocal(String idName)
        Sym lookup_result;
        try {
            symTable.addScope();
            symTable.addDecl("id1", sym1);
            lookup_result = symTable.lookupLocal("id1");
            if (lookup_result == null)
                throw new Exception();
        }
        catch (Exception ex) {
            System.out.println("[lookupLocal] fail to look up the id");
        }

        // test proper cases for lookupGlobal(String idName), in a closet
        // scope, in the middle scope and in the farthest scope.
        // first add scopes for the table.
        try{
            Sym sym2 = new Sym("b");
            Sym sym3 = new Sym("c");
            symTable.addScope();
            symTable.addDecl("id2", sym2);
            symTable.addScope();
            symTable.addDecl("id3", sym3);
        }
        catch (Exception ex) {
            System.out.println("Unexpected exception when creating " +
                    "multi-scope");
        }

        // print out layers
        try {
            System.out.println("[3] <multi-scope case> layout");
            symTable.print();
        } catch (Exception ex) {
            System.out.println("[print] unexpected exception when" +
                    " printing multi-scope case");
        }

        // look up a key in the closet scope
        try{
            lookup_result = symTable.lookupGlobal("id3");
            if (lookup_result == null)
                throw new Exception();
        } catch (Exception ex) {
            System.out.println("[lookupGlobal] fail in the closet scope");
        }

        // look up a key in the middle scope
        try{
            lookup_result = symTable.lookupGlobal("id2");
            if (lookup_result == null)
                throw new Exception();
        }
        catch (Exception ex) {
            System.out.println("[lookupGlobal] fail in a middle scope");
        }

        // look up a key in the farthest scope
        try{
            lookup_result = symTable.lookupGlobal("id1");
            if (lookup_result == null)
                throw new Exception();
        }
        catch (Exception ex) {
            System.out.println("[lookupGlobal] fail in the farthest scope");
        }  // finish test lookupGlobal(String idName);


    }
} // end of P1 class
