package com.numble.team3.security;

import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
  private final String accountId;
  private final Set<GrantedAuthority> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getUsername() {
    return accountId;
  }

  @Override
  public boolean isAccountNonExpired() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAccountNonLocked() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEnabled() {
    throw new UnsupportedOperationException();
  }
}
