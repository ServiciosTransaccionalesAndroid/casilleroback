package com.servientrega.locker.security;

import com.servientrega.locker.repository.AdminRepository;
import com.servientrega.locker.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Primary
@RequiredArgsConstructor
public class UnifiedUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final CourierRepository courierRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Try admin first (email)
        var admin = adminRepository.findByEmail(username);
        if (admin.isPresent()) {
            var a = admin.get();
            if (!a.getActive()) {
                throw new UsernameNotFoundException("Admin account is inactive");
            }
            return User.builder()
                    .username(a.getEmail())
                    .password(a.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }

        // Try courier (employeeId)
        var courier = courierRepository.findByEmployeeId(username);
        if (courier.isPresent()) {
            var c = courier.get();
            if (!c.getActive()) {
                throw new UsernameNotFoundException("Courier account is inactive");
            }
            return User.builder()
                    .username(c.getEmployeeId())
                    .password(c.getPin())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_COURIER")))
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
