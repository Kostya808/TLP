package supporting_structures;

public class DeclaredVar {
    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeclaredVar(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
