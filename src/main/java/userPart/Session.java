package userPart;

public class Session {
    private final String userName;
    private final String password;
    private boolean isAuthorized;

    public Session(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}
