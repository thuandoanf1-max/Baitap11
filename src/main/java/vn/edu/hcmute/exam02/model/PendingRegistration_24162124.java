package vn.edu.hcmute.exam02.model;

import java.io.Serializable;

public class PendingRegistration_24162124 implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String email;
    private final String fullname;
    private final Integer phone;
    private final String password;

    public PendingRegistration_24162124(String email, String fullname,
                                        Integer phone, String password) {
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public Integer getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}
