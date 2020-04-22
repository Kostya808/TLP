package AST;

import java.util.List;

public class AST {
    private String token;
    private String typeToken;
    private int row;
    private int col;
    private AST parent;
    private List<AST> children;

//    public AST() { }

    public AST(AST node) {
        this.token = node.getToken();
        this.typeToken = node.getTypeToken();
        this.row = node.row;
        this.col = node.col;
        this.parent = node.getParent();
        this.children = node.getChildren();
    }

    public AST(String token, String typeToken, int row, int col, AST parent, List<AST> children) {
        this.token = token;
        this.typeToken = typeToken;
        this.row = row;
        this.col = col;
        this.parent = parent;
        this.children = children;
    }

    public AST(String token, String typeToken) {
        this.token = token;
        this.typeToken = typeToken;
    }

    public AST() { }

    public void add_children(AST child) {
        children.add(child);
    }

    public static void print_tree(AST node, int indent, int depth) {
        System.out.println("< '" + node.getToken() + "' : '" + node.getTypeToken() + "' " + node.getRow() + " : " + node.getCol()+ ">");
        if(depth > 0) {
            if (!node.getChildren().isEmpty()) {
                for (AST s : node.getChildren()) {
                    for (int i = 0; i <= indent; i++) {
                        System.out.print(" ");
                        if(0 == i % 5)
                            System.out.print("|");
                    }
                    print_tree(s, indent + 5, depth - 1);
                }
            }
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTypeToken(String typeToken) {
        this.typeToken = typeToken;
    }

    public void setParent(AST parent) {
        this.parent = parent;
    }

    public void setChildren(List<AST> children) {
        this.children = children;
    }

    public String getToken() {
        return token;
    }

    public String getTypeToken() {
        return typeToken;
    }

    public AST getParent() {
        return parent;
    }

    public List<AST> getChildren() {
        return children;
    }

    public void setRow(int row) { this.row = row; }

    public void setCol(int col) { this.col = col; }

    public int getRow() { return row; }

    public int getCol() { return col; }
}
