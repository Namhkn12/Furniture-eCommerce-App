package api.user.request;

public class CodeRequest {
    private final String username;
    private final String code;

    public CodeRequest(String username, String code) {
        this.username = username;
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public String getCode() {
        return code;
    }
}
