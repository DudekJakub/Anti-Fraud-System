package antifraud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class UserDetailsImpl implements UserDetails, UserDetailsMixin {

    private String name;
    private String username;
    private String password;
    private boolean accountNonLocked;
    private List<GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.accountNonLocked = user.isAccountNonLocked();
        this.authorities = Arrays.stream(user.getRole().toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
}
