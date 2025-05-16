package api.user.request;

import java.time.LocalDate;

public class UpdateDOBRequest {
    private LocalDate dateOfBirth;

    public UpdateDOBRequest(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
