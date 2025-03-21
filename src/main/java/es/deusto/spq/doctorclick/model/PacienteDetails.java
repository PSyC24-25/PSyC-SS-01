package es.deusto.spq.doctorclick.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class PacienteDetails implements UserDetails {
    private Paciente paciente;

    public PacienteDetails(Paciente paciente) {
        this.paciente = paciente;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_PACIENTE");
    }

    @Override
    public String getUsername() {
        return paciente.getDni();
    }

    @Override
    public String getPassword() {
        return paciente.getContrasenia();
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
}
