package api.user.request;

public class UpdateEmailRequest {
    private final String username;
    private final String newEmail;
    private final String code;

    public UpdateEmailRequest(String username, String newEmail, String code) {
        this.username = username;
        this.newEmail = newEmail;
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public String getCode() {
        return code;
    }
}
