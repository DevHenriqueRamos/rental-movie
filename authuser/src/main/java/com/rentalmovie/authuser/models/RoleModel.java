package com.rentalmovie.authuser.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rentalmovie.authuser.enums.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_ROLES")
public class RoleModel implements GrantedAuthority, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID roleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, unique = true)
    private RoleType roleName;

    @Override
    @JsonIgnore
    public String getAuthority() {
        return this.roleName.name();
    }
}
