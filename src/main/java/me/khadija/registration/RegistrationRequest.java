package me.khadija.registration;

public record RegistrationRequest(String firstName,
                                  String lastName,
                                  String username,
                                  String password,
                                  String email) {

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
