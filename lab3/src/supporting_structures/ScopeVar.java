package supporting_structures;

import AST.AST;

public class ScopeVar {
    private AST node;
    private String scope;

    public ScopeVar(AST node, String scope) {
        this.node = node;
        this.scope = scope;
    }

    public void setNode(AST node) {
        this.node = node;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public AST getNode() {
        return node;
    }

    public String getScope() {
        return scope;
    }
}
