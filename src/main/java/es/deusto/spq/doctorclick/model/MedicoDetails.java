package es.deusto.spq.doctorclick.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MedicoDetails implements UserDetails {
    private Medico medico;

    public MedicoDetails(Medico medico) {
        this.medico = medico;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_MEDICO");
    }

    @Override
    public String getUsername() {
        return medico.getDni();
    }

    @Override
    public String getPassword() {
        return medico.getContrasenia();
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
