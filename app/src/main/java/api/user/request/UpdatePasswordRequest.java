package api.user.request;

public class UpdatePasswordRequest {
    private final String username;
    private final String code;
    private final String newPassword;

    public UpdatePasswordRequest(String username, String code, String newPassword) {

        this.username = username;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getCode() {
        return code;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
