package org.example.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_name"})
})
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    public Role() {
        System.out.println("Role default constructor called ");
    }

    public Role(String roleName, String displayName) {
        System.out.println("Role standard constructor called ");
        this.roleName = roleName;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }

    @Override
    public String toString() {
        return super.toString() + " Role{" +
                "id='" + id + '\'' +
                "roleName='" + roleName + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}