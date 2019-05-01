import java.util.*;

public class Sym {
    private String type;
    
    public Sym(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public String toString() {
        return type;
    }

    public boolean ifExists(String name) {return true;}
}

class funcSym extends Sym {
    private Sym returnSym;
    private List<Sym> mySyms;//linked list of types of formals

    public funcSym(Sym returnSym, List<Sym> mySyms) {
        super("function");
        this.returnSym = returnSym;
        this.mySyms = mySyms;
    }

    public Sym getReturnSym() {
        return returnSym;
    }

    public List<Sym> getFormals() {
        return mySyms;
    }

    public boolean ifExists(String name) { return true; }

}

class structSym extends Sym {
    public String myName;
    public SymTable myField;

    public structSym(String name) {
        super("struct");
        myName = name;
        myField = new SymTable();
        myField.addScope();
    }

    public boolean ifExists(String name) {
        if (myField.lookupLocal(name) == null) {
            return false;
        } else { return true; }
    }

}
