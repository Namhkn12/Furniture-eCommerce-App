package api.user.request;

public class ChangePasswordRequest {
    private final String username;

    public ChangePasswordRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
