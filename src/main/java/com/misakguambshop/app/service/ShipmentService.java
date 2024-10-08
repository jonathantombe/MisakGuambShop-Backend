package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.ShipmentDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.Shipment;
import com.misakguambshop.app.model.ShipmentStatus;
import com.misakguambshop.app.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    public List<ShipmentDto> getAllShipments() {
        List<Shipment> shipments = shipmentRepository.findAll();
        return shipments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ShipmentDto getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment no encontrado con ID: " + id));
        return convertToDto(shipment);
    }

    @Transactional
    public ShipmentDto createShipment(ShipmentDto shipmentDto) {
        validateShipment(shipmentDto);
        Shipment shipment = convertToEntity(shipmentDto);
        shipment.setStatus(ShipmentStatus.fromString(shipmentDto.getStatus().name()));
        Shipment savedShipment = shipmentRepository.save(shipment);
        return convertToDto(savedShipment);
    }

    @Transactional
    public ShipmentDto updateShipment(Long id, ShipmentDto shipmentDto) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment no encontrado con ID: " + id));


        validateStatusTransition(existingShipment.getStatus(), ShipmentStatus.fromString(shipmentDto.getStatus().name()));

        existingShipment.setAddress(shipmentDto.getAddress());
        existingShipment.setRecipientName(shipmentDto.getRecipientName());
        existingShipment.setPhoneNumber(shipmentDto.getPhoneNumber());
        existingShipment.setEmail(shipmentDto.getEmail());
        existingShipment.setShippingMethod(shipmentDto.getShippingMethod());
        existingShipment.setShippingDate(shipmentDto.getShippingDate());
        existingShipment.setEstimatedDeliveryDate(shipmentDto.getEstimatedDeliveryDate());
        existingShipment.setActualDeliveryDate(shipmentDto.getActualDeliveryDate());
        existingShipment.setCountry(shipmentDto.getCountry());
        existingShipment.setCity(shipmentDto.getCity());
        existingShipment.setStatus(ShipmentStatus.fromString(shipmentDto.getStatus().name()));
        existingShipment.setWeight(shipmentDto.getWeight());
        existingShipment.setShippingCost(shipmentDto.getShippingCost());
        existingShipment.setInsuranceCost(shipmentDto.getInsuranceCost());
        existingShipment.setShippingCompany(shipmentDto.getShippingCompany());

        Shipment updatedShipment = shipmentRepository.save(existingShipment);
        return convertToDto(updatedShipment);
    }

    @Transactional
    public void deleteShipment(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment no encontrado con ID: " + id));
        shipmentRepository.delete(shipment);
    }

    private void validateShipment(ShipmentDto shipmentDto) {
        if (!isCityValidForCountry(shipmentDto.getCity(), shipmentDto.getCountry())) {
            throw new IllegalArgumentException("La ciudad no es válida para el país seleccionado.");
        }
        if (shipmentDto.getEstimatedDeliveryDate().isBefore(shipmentDto.getShippingDate())) {
            throw new IllegalArgumentException("La fecha de entrega estimada no puede ser anterior a la fecha de envío.");
        }
    }

    private boolean isCityValidForCountry(String city, String country) {
        return country.equalsIgnoreCase("Colombia") && (city.equalsIgnoreCase("Bogotá") || city.equalsIgnoreCase("Cali"));
    }

    private void validateStatusTransition(ShipmentStatus currentStatus, ShipmentStatus newStatus) {
        if (currentStatus.equals(ShipmentStatus.PENDING) && newStatus.equals(ShipmentStatus.DELIVERED)) {
            throw new IllegalArgumentException("No se puede pasar de 'PENDING' a 'DELIVERED' directamente.");
        }
        if (currentStatus.equals(ShipmentStatus.RETURNED)) {
            throw new IllegalArgumentException("Un envío devuelto no puede cambiar de estado.");
        }
    }

    private ShipmentDto convertToDto(Shipment shipment) {
        ShipmentDto shipmentDto = new ShipmentDto();
        shipmentDto.setId(shipment.getId());
        shipmentDto.setOrderId(shipment.getOrderId());
        shipmentDto.setAddress(shipment.getAddress());
        shipmentDto.setRecipientName(shipment.getRecipientName());
        shipmentDto.setPhoneNumber(shipment.getPhoneNumber());
        shipmentDto.setEmail(shipment.getEmail());
        shipmentDto.setShippingMethod(shipment.getShippingMethod());
        shipmentDto.setShippingDate(shipment.getShippingDate());
        shipmentDto.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        shipmentDto.setActualDeliveryDate(shipment.getActualDeliveryDate());
        shipmentDto.setCountry(shipment.getCountry());
        shipmentDto.setCity(shipment.getCity());
        shipmentDto.setStatus(shipment.getStatus());
        shipmentDto.setWeight(shipment.getWeight());
        shipmentDto.setShippingCost(shipment.getShippingCost());
        shipmentDto.setInsuranceCost(shipment.getInsuranceCost());
        shipmentDto.setShippingCompany(shipment.getShippingCompany());
        return shipmentDto;
    }

    private Shipment convertToEntity(ShipmentDto shipmentDto) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(shipmentDto.getOrderId());
        shipment.setAddress(shipmentDto.getAddress());
        shipment.setRecipientName(shipmentDto.getRecipientName());
        shipment.setPhoneNumber(shipmentDto.getPhoneNumber());
        shipment.setEmail(shipmentDto.getEmail());
        shipment.setShippingMethod(shipmentDto.getShippingMethod());
        shipment.setShippingDate(shipmentDto.getShippingDate());
        shipment.setEstimatedDeliveryDate(shipmentDto.getEstimatedDeliveryDate());
        shipment.setActualDeliveryDate(shipmentDto.getActualDeliveryDate());
        shipment.setCountry(shipmentDto.getCountry());
        shipment.setCity(shipmentDto.getCity());
        shipment.setStatus(ShipmentStatus.fromString(shipmentDto.getStatus().name()));
        shipment.setWeight(shipmentDto.getWeight());
        shipment.setShippingCost(shipmentDto.getShippingCost());
        shipment.setInsuranceCost(shipmentDto.getInsuranceCost());
        shipment.setShippingCompany(shipmentDto.getShippingCompany());
        return shipment;
    }
}