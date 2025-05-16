package api.user.request;

public class UpdateNameRequest {
    private final String displayName;

    public UpdateNameRequest(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}