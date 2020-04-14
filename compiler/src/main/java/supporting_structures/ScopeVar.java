package supporting_structures;

import AST.AST;

public class ScopeVar {
    private AST node;
    private String scope;
    private String type;

    public ScopeVar(AST node, String scope, String type) {
        this.node = node;
        this.scope = scope;
        this.type = type;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
