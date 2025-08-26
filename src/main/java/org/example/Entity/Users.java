package org.example.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users") // Use @Document for MongoDB
public class Users implements UserDetails {

    @Id
    private String id; // MongoDB IDs are Strings

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Indexed(unique = true) // Creates a unique index for the email field
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private String role = "USER";

    private String avatar;

    private String token;

    private boolean isEnabled = true;

    @DBRef(lazy = true) // Use @DBRef to link to other documents
    @Builder.Default
    private List<BaseLayout> submittedLayouts = new ArrayList<>();

    // --- Helper methods for relationship management ---
    public void addBaseLayout(BaseLayout layout) {
        if (this.submittedLayouts == null) {
            this.submittedLayouts = new ArrayList<>();
        }
        this.submittedLayouts.add(layout);
        layout.setSubmittedBy(this);
    }

    public void removeBaseLayout(BaseLayout layout) {
        if (this.submittedLayouts != null) {
            this.submittedLayouts.remove(layout);
            layout.setSubmittedBy(null);
        }
    }

    // --- UserDetails implementation ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.toUpperCase()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
