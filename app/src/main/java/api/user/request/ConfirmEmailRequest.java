package api.user.request;

public class ConfirmEmailRequest {
    private final String email;
    private final String username;

    public ConfirmEmailRequest(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
