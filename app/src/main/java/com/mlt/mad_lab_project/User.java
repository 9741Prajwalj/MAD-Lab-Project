package com.mlt.mad_lab_project;

public class User {
    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Override equals and hashCode for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equalsIgnoreCase(user.email);
    }

    @Override
    public int hashCode() {
        return email.toLowerCase().hashCode();
    }
}