package supporting_structures;

public class Register {
    private String name;
    private boolean employment;

    public Register(String name, boolean employment) {
        this.name = name;
        this.employment = employment;
    }

    public String getName() {
        return name;
    }

    public boolean isEmployment() {
        return employment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmployment(boolean employment) {
        this.employment = employment;
    }
}
