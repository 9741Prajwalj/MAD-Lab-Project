package com.mlt.mad_lab_project;

public class Admin {
    private String name;
    private String email;
    private String password;

    public Admin(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return email.equalsIgnoreCase(admin.email);
    }

    @Override
    public int hashCode() {
        return email.toLowerCase().hashCode();
    }
}