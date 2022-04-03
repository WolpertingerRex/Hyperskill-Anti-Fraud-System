package antifraud.security;

public enum Role {
    ADMINISTRATOR,
    MERCHANT,
    SUPPORT;

    public String getRole(){
        return "ROLE_" + this.name();
    }
}
