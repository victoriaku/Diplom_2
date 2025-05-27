package site.nomoreparties.stellarburgers.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserModel {
    private String email;
    private String password;
    private String name;

    public UserModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
