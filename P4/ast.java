import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a Carrot program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// %%%ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode { 
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void addIndent(PrintWriter p, int indent) {
        for (int k = 0; k < indent; k++) p.print(" ");
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
        mySymTable = new SymTable();
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    public void nameAnalysis() {
        mySymTable.addScope();
        myDeclList.nameAnalysis(mySymTable);
        try {
            mySymTable.removeScope();
        } catch (Exception e) {}
    }

    // 1 kid
    private DeclListNode myDeclList;
    private SymTable mySymTable;
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    public void nameAnalysis(SymTable programSymTable) {
        Iterator<DeclNode> it = myDecls.iterator();
        while (it.hasNext()) {
            DeclNode d = (DeclNode)it.next();
            d.nameAnalysis(programSymTable);
        }
    }

    // list of kids (DeclNodes)
    private List<DeclNode> myDecls;
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    public void nameAnalysis(SymTable programSymTable) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().nameAnalysis(programSymTable);
            while (it.hasNext()) {
                it.next().nameAnalysis(programSymTable);
            }
        }
    }

    public void getSymList(List<Sym> target) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            target.add(it.next().getSym());
            while (it.hasNext()) {
                target.add(it.next().getSym());
            }
        }
    }

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    public void nameAnalysis(SymTable programSymTable) {
        myDeclList.nameAnalysis(programSymTable);
        myStmtList.nameAnalysis(programSymTable);
    }

    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    public void nameAnalysis(SymTable programSymTable) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().nameAnalysis(programSymTable);
        }
    }

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparseForUse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparseForUse(p, indent);
            }
        } 
    }

    public void nameAnalysis(SymTable programSymTable) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().nameAnalysisForUse(programSymTable);
            while (it.hasNext()) {  // print the rest of the list
                it.next().nameAnalysisForUse(programSymTable);
            }
        }
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    abstract public void nameAnalysis(SymTable programSymTable);
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    public void nameAnalysis(SymTable programSymTable) {
        if(myType.getType().toString() == "struct") {
            //System.out.println(myId.getType());
           if(programSymTable.lookupGlobal(myType.getName()) == null)
               ErrMsg.fatal(myId.getMyLineNum(),myId.getMyCharNum(), "Invalid name of struct type");
        }
        myId.nameAnalysis(programSymTable, myType.getType());
        // System.out.println(myId.getType());
    }

    // 3 kids
    private TypeNode myType;
    private IdNode myId;
    public int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myId.myFuncName =  true;
        myFormalsList = formalList;
        myBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" "+ myId.getMyStrVal());
        //myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    public void nameAnalysis(SymTable programSymTable) {
        List<Sym> mySyms = new ArrayList<Sym>();
        myFormalsList.getSymList(mySyms);
        funcSym myFuncSym = new funcSym(myType.getType(), mySyms);
        myId.nameAnalysis(programSymTable, myFuncSym);
        programSymTable.addScope();
        myFormalsList.nameAnalysis(programSymTable);
        myBody.nameAnalysis(programSymTable);
        try {
            programSymTable.removeScope();
        } catch (Exception e) {
            //
        }
    }

    // 4 kids
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    public void nameAnalysis(SymTable programSymTable) {
        myId.nameAnalysis(programSymTable, myType.getType());
    }

    public Sym getSym() { return myType.getType(); }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
        myId.myStructName = true;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("struct ");
        myId.unparse(p, 0);
        p.println("{");
        myDeclList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("};\n");

    }

    public void nameAnalysis(SymTable programSymTable) {
        structSym myStructSym = new structSym(myId.getMyStrVal());
        myDeclList.nameAnalysis(myStructSym.myField);
        myId.nameAnalysis(programSymTable, myStructSym);
    }

    // 2 kids
    private IdNode myId;
    private DeclListNode myDeclList;
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    abstract public Sym getType();
    abstract public String getName();
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }

    public Sym getType() {
        Sym myType = new Sym("int");
        return myType;
    }

    public String getName() { return "int"; }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }

    public Sym getType() {
        Sym myType = new Sym("bool");
        return myType;
    }

    public String getName() { return "bool"; }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }

    public Sym getType() {
        Sym myType = new Sym("void");
        return myType;
    }

    public String getName() { return "void"; }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }

    public Sym getType() {
        Sym myType = new structSym(myId.getMyStrVal());//check whether struct type defined;
        return myType;
    }

    public String getName() {
        return myId.getMyStrVal();
    }
    
    // 1 kid
    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
    abstract public void nameAnalysis(SymTable programSymTable);
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myAssign.nameAnalysis(programSymTable);
    }

    // 1 kid
    private AssignNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myExp.unparseForUse(p, 0);
        p.println("++;");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myExp.nameAnalysisForUse(programSymTable);
    }

    // 1 kid
    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myExp.unparseForUse(p, 0);
        p.println("--;");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myExp.nameAnalysisForUse(programSymTable);
    }

    // 1 kid
    private ExpNode myExp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("cin >> ");
        myExp.unparseForUse(p, 0);
        p.println(";");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myExp.nameAnalysisForUse(programSymTable);
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("cout << ");
        myExp.unparseForUse(p, 0);
        p.println(";");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myExp.nameAnalysisForUse(programSymTable);
    }

    // 1 kid
    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("if (");
        myExp.unparseForUse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myExp.nameAnalysisForUse(programSymTable);
        programSymTable.addScope();
        myDeclList.nameAnalysis(programSymTable);
        myStmtList.nameAnalysis(programSymTable);
        try {
            programSymTable.removeScope();
        } catch (Exception e) {
            //
        }
    }

    // e kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("if (");
        myExp.unparseForUse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
        addIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");        
    }

    public void nameAnalysis(SymTable programSymTable) {
        myExp.nameAnalysisForUse(programSymTable);
        programSymTable.addScope();
        myThenDeclList.nameAnalysis(programSymTable);
        myThenStmtList.nameAnalysis(programSymTable);
        try {
            programSymTable.removeScope();
        } catch (Exception e) {
            //
        }
        programSymTable.addScope();
        myElseDeclList.nameAnalysis(programSymTable);
        myElseStmtList.nameAnalysis(programSymTable);
        try {
            programSymTable.removeScope();
        } catch (Exception e) {
            //
        }
    }

    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
    
    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("while (");
        myExp.unparseForUse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myExp.nameAnalysisForUse(programSymTable);
        programSymTable.addScope();
        myDeclList.nameAnalysis(programSymTable);
        myStmtList.nameAnalysis(programSymTable);
        try {
            programSymTable.removeScope();
        } catch (Exception e) {
            //
        }
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
	addIndent(p, indent);
        p.print("repeat (");
        myExp.unparseForUse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myExp.nameAnalysisForUse(programSymTable);
        programSymTable.addScope();
        myDeclList.nameAnalysis(programSymTable);
        myStmtList.nameAnalysis(programSymTable);
        try {
            programSymTable.removeScope();
        } catch (Exception e) {
            //
        }
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myCall.nameAnalysis(programSymTable);
    }

    // 1 kid
    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparseForUse(p, 0);
        }
        p.println(";");
    }

    public void nameAnalysis(SymTable programSymTable) {
        if (myExp != null) {
            myExp.nameAnalysisForUse(programSymTable);
        }
    }

    // 1 kid
    private ExpNode myExp; // possibly null
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
    abstract public void nameAnalysisForUse(SymTable programSymTable);
    abstract public Sym getType();
    public void unparseForUse(PrintWriter p, int indent) {unparse(p, indent);}
    public int getMyLineNum() { return 0; }
    public int getMyCharNum() { return 0; }

    static public boolean flag = false;
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }
    public void unparseForUse(PrintWriter p, int indent) { p.print(myIntVal); }

    public void nameAnalysisForUse(SymTable programSymTable) {};
    public Sym getType() {return null;}

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }
    public void unparseForUse(PrintWriter p, int indent) { p.print(myStrVal); }

    public void nameAnalysisForUse(SymTable programSymTable) {};
    public Sym getType() {return null;}

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }
    public void unparseForUse(PrintWriter p, int indent) { p.print("true"); }

    public void nameAnalysisForUse(SymTable programSymTable) {};
    public Sym getType() {return null;}

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }
    public void unparseForUse(PrintWriter p, int indent) { p.print("false"); }

    public void nameAnalysisForUse(SymTable programSymTable) {};
    public Sym getType() {return null;}

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) { p.print(myStrVal); }

    public void unparseForUse(PrintWriter p, int indent) {

        p.print(myStrVal);
        p.print("(" );
        if (mySym != null) {
            if (mySym.getType() == "struct")
                System.out.println(getMyStrVal());
            if (mySym.getType() == "function") {
                funcSym funSym = (funcSym) mySym;
                Iterator<Sym> it = funSym.getFormals().iterator();
                if (it.hasNext()) { // if there is at least one element
                    p.print(it.next().getType());
                    while (it.hasNext())
                        p.print(", " + it.next().getType());
                }
                p.print("->" + funSym.getReturnSym().getType());
            }
            else
                p.print(mySym.getType());
        }
        p.print(")");
    }


    public void nameAnalysis(SymTable programSymTable, Sym myType) {
        try {
            if (programSymTable.lookupLocal(myStrVal) != null)
                ErrMsg.fatal(myLineNum,myCharNum, "Multiply declared identifier");
            if (myType.getType() == "void" && !myFuncName)
                ErrMsg.fatal(myLineNum, myCharNum, "Non-function declared void");
            programSymTable.addDecl(myStrVal, myType);
        } catch (Exception e) {}
    }

    public void nameAnalysisForUse(SymTable programSymTable) {

        mySym = programSymTable.lookupGlobal(myStrVal);
        if (mySym == null)
            ErrMsg.fatal(myLineNum,myCharNum, "Undeclared identifier");
//        else if (mySym.getType() != "struct") {
//            if(flag)
//                ErrMsg.fatal(myLineNum, myCharNum, "Dot-access of non-struct type");
//        }
    }

    public Sym getType() {return mySym;}

    public String getMyStrVal() { return myStrVal; }

    public int getMyLineNum() { return myLineNum; }
    public int getMyCharNum() { return myCharNum; }

    public boolean myFuncName = false;
    public boolean myStructName = false;
    private Sym mySym;
    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparseForUse(p, 0);
        p.print(").");
        myId.unparseForUse(p, 0);
    }

    public void nameAnalysisForUse(SymTable programSymTable) {
        flag = true;
        myLoc.nameAnalysisForUse(programSymTable);
        flag = false;
        if (myLoc.getType() == null || myLoc.getType().getType() != "struct")
            ErrMsg.fatal(myLoc.getMyLineNum(), myLoc.getMyCharNum(), "Dot-access of non-struct type");
        else {
            if (!myLoc.getType().ifExists(myId.getMyStrVal())) {
                ErrMsg.fatal(myLoc.getMyLineNum(), myLoc.getMyCharNum(), "Invalid struct field name");
            }
            myId.nameAnalysisForUse(((structSym)myLoc.getType()).myField);
        }
    }
    public Sym getType() {return myId.getType();}

    // 2 kids
    private ExpNode myLoc;    
    private IdNode myId;

    private int myLineNum;
    private int myCharNum;
    private Sym mySym;
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparseForUse(p, 0);
        p.print(" = ");
        myExp.unparseForUse(p, 0);
        if (indent != -1)  p.print(")");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myLhs.nameAnalysisForUse(programSymTable);
        myExp.nameAnalysisForUse(programSymTable);
    }

    public void nameAnalysisForUse(SymTable programSymTable) {};
    public Sym getType() {return myExp.getType();}

    // 2 kids
    private ExpNode myLhs;
    private ExpNode myExp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
        myId.unparseForUse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    public void nameAnalysis(SymTable programSymTable) {
        myId.nameAnalysisForUse(programSymTable);
        myExpList.nameAnalysis(programSymTable);
    }

    public void nameAnalysisForUse(SymTable programSymTable) {};

    public Sym getType() { return null; };

    // 2 kids
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }
    public void nameAnalysisForUse(SymTable programSymTable) {};
    //public void unparseForUse(PrintWriter p, int indent) {unparse(p, indent);};

    public Sym getType() {return null;}

    // one child
    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    public void nameAnalysisForUse(SymTable programSymTable) {};
    public Sym getType() {return null;}

    // two kids
    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        System.out.println("here");
        myExp.nameAnalysisForUse(programSymTable);
    }
    public void unparseForUse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparseForUse(p, 0);
        p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparseForUse(p, 0);
        p.print(")");
    }
    public void unparseForUse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparseForUse(p, 0);
        p.print(")");
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" + ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" - ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" * ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" / ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" && ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" || ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" == ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" != ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" < ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" > ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" <= ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparseForUse(p, 0);
        p.print(" >= ");
        myExp2.unparseForUse(p, 0);
        p.print(")");
    }
    public void nameAnalysisForUse(SymTable programSymTable) {
        myExp1.nameAnalysisForUse(programSymTable);
        myExp2.nameAnalysisForUse(programSymTable);
    }
}
