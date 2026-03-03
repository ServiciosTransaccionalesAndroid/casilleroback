package com.servientrega.locker.service;

import com.servientrega.locker.dto.CourierDTO;
import com.servientrega.locker.entity.Courier;
import com.servientrega.locker.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CourierDTO.CourierResponse create(CourierDTO.CreateRequest request) {
        if (courierRepository.findByEmployeeId(request.employeeId()).isPresent()) {
            throw new RuntimeException("Employee ID already exists");
        }

        Courier courier = new Courier();
        courier.setEmployeeId(request.employeeId());
        courier.setName(request.name());
        courier.setPhone(request.phone());
        courier.setEmail(request.email());
        courier.setPin(passwordEncoder.encode(request.pin()));
        courier.setActive(true);

        Courier saved = courierRepository.save(courier);
        return toResponse(saved);
    }

    @Transactional
    public CourierDTO.CourierResponse update(String employeeId, CourierDTO.UpdateRequest request) {
        Courier courier = courierRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new RuntimeException("Courier not found"));

        if (request.name() != null) courier.setName(request.name());
        if (request.phone() != null) courier.setPhone(request.phone());
        if (request.email() != null) courier.setEmail(request.email());
        if (request.pin() != null) courier.setPin(passwordEncoder.encode(request.pin()));
        if (request.active() != null) courier.setActive(request.active());

        Courier updated = courierRepository.save(courier);
        return toResponse(updated);
    }

    @Transactional
    public void delete(String employeeId) {
        Courier courier = courierRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new RuntimeException("Courier not found"));
        courierRepository.delete(courier);
    }

    public CourierDTO.CourierResponse getByEmployeeId(String employeeId) {
        Courier courier = courierRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new RuntimeException("Courier not found"));
        return toResponse(courier);
    }

    public List<CourierDTO.CourierResponse> getAll() {
        return courierRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private CourierDTO.CourierResponse toResponse(Courier courier) {
        return new CourierDTO.CourierResponse(
            courier.getId(),
            courier.getEmployeeId(),
            courier.getName(),
            courier.getPhone(),
            courier.getEmail(),
            courier.getActive()
        );
    }
}
