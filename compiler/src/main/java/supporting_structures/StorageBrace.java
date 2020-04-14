package supporting_structures;

public class StorageBrace {
    private String brace;
    private int indBrace;

    public StorageBrace(String brace, int indBrace) {
        this.brace = brace;
        this.indBrace = indBrace;
    }

    public void setIndBrace(int indBrace) {
        this.indBrace = indBrace;
    }

    public void setBrace(String brace) {
        this.brace = brace;
    }

    public String getBrace() {
        return brace;
    }

    public int getIndBrace() {
        return indBrace;
    }
}
