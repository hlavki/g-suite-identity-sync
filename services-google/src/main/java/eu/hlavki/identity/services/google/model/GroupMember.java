package eu.hlavki.identity.services.google.model;

import java.util.Objects;

public class GroupMember {

    public enum Status {
        ACTIVE, SUSPENDED, UNKNOWN
    }

    public enum Type {
        CUSTOMER, EXTERNAL, USER, GROUP
    }
    private String id;
    private String email;
    private String kind;
    private String role;
    private String etag;
    private Status status;
    private Type type;


    public GroupMember() {
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getKind() {
        return kind;
    }


    public void setKind(String kind) {
        this.kind = kind;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public String getEtag() {
        return etag;
    }


    public void setEtag(String etag) {
        this.etag = etag;
    }


    public Status getStatus() {
        return status;
    }


    public void setStatus(Status status) {
        this.status = status;
    }


    public Type getType() {
        return type;
    }


    public void setType(Type type) {
        this.type = type;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.email);
        return hash;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final GroupMember other = (GroupMember) obj;
        if (!Objects.equals(this.email, other.email)) return false;
        return true;
    }


    @Override
    public String toString() {
        return StringUtils.toStringLine(this);
    }
}
