package models;

import java.sql.Date;
import java.sql.Timestamp;

public class User {
    private String username;
    private String password;
    private String first;
    private String last;
    private String email;
    private Date dateOfBirth;
    private Timestamp lastAccessedDate;
    private Timestamp creationDate;

    // Constructor
    public User(String username, String password, String first, String last,
                String email, Date dateOfBirth, Timestamp lastAccessedDate, Timestamp creationDate) {
        this.username = username;
        this.password = password;
        this.first = first;
        this.last = last;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.lastAccessedDate = lastAccessedDate;
        this.creationDate = creationDate;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirst() { return first; }
    public void setFirst(String first) { this.first = first; }

    public String getLast() { return last; }
    public void setLast(String last) { this.last = last; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Timestamp getLastAccessedDate() { return lastAccessedDate; }
    public void setLastAccessedDate(Timestamp lastAccessedDate) { this.lastAccessedDate = lastAccessedDate; }

    public Timestamp getCreationDate() { return creationDate; }
    public void setCreationDate(Timestamp creationDate) { this.creationDate = creationDate; }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", lastAccessedDate=" + lastAccessedDate +
                ", creationDate=" + creationDate +
                '}';
    }
}
