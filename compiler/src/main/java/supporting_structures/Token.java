package supporting_structures;

public class Token {
    private String token;
    private String typeToken;
    private int str;
    private int col;

    public Token(String token, String typeToken, int str, int col) {
        this.token = token;
        this.typeToken = typeToken;
        this.str = str;
        this.col = col;
    }

    public void setLineNumber(int lineNumber) {
        this.str = lineNumber;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTypeToken(String typeToken) {
        this.typeToken = typeToken;
    }

    public int getLineNumber() {
        return str;
    }

    public String getToken() {
        return token;
    }

    public String getTypeToken() {
        return typeToken;
    }

    public void setStr(int str) { this.str = str; }

    public void setCol(int col) { this.col = col; }

    public int getStr() { return str; }

    public int getCol() { return col; }
}
