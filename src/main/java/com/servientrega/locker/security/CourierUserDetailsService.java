package com.servientrega.locker.security;

import com.servientrega.locker.entity.Courier;
import com.servientrega.locker.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CourierUserDetailsService implements UserDetailsService {

    private final CourierRepository courierRepository;

    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        Courier courier = courierRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("Courier not found: " + employeeId));

        if (!courier.getActive()) {
            throw new UsernameNotFoundException("Courier account is inactive");
        }

        return User.builder()
                .username(courier.getEmployeeId())
                .password(courier.getPin())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_COURIER")))
                .build();
    }
}
