package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.ShipmentDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.Shipment;
import com.misakguambshop.app.model.ShipmentStatus;
import com.misakguambshop.app.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
        Shipment savedShipment = shipmentRepository.save(shipment);
        return convertToDto(savedShipment);
    }

    @Transactional
    public ShipmentDto updateShipment(Long id, ShipmentDto shipmentDto) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment no encontrado con ID: " + id));

        existingShipment.setAddress(shipmentDto.getAddress());
        existingShipment.setRecipientName(shipmentDto.getRecipientName());
        existingShipment.setPhoneNumber(shipmentDto.getPhoneNumber());
        existingShipment.setEmail(shipmentDto.getEmail());
        existingShipment.setDepartment(shipmentDto.getDepartment());
        existingShipment.setCity(shipmentDto.getCity());
        existingShipment.setNeighborhood(shipmentDto.getNeighborhood());
        existingShipment.setPostalCode(shipmentDto.getPostalCode());
        existingShipment.setShippingNotes(shipmentDto.getShippingNotes());

        Shipment updatedShipment = shipmentRepository.save(existingShipment);
        return convertToDto(updatedShipment);
    }

    @Transactional
    public ShipmentDto partialUpdateShipment(Long id, Map<String, Object> updates) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment no encontrado con ID: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "address":
                    existingShipment.setAddress((String) value);
                    break;
                case "recipientName":
                    existingShipment.setRecipientName((String) value);
                    break;
                case "phoneNumber":
                    existingShipment.setPhoneNumber((String) value);
                    break;
                case "email":
                    existingShipment.setEmail((String) value);
                    break;
                case "department":
                    existingShipment.setDepartment((String) value);
                    break;
                case "city":
                    existingShipment.setCity((String) value);
                    break;
                case "neighborhood":
                    existingShipment.setNeighborhood((String) value);
                    break;
                case "postalCode":
                    existingShipment.setPostalCode((String) value);
                    break;
                case "shippingNotes":
                    existingShipment.setShippingNotes((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("Propiedad desconocida: " + key);
            }
        });

        validateShipment(convertToDto(existingShipment));
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
        // Validaciones de envío
    }

    private void validateStatusTransition(ShipmentStatus currentStatus, ShipmentStatus newStatus) {
        // Validaciones de transición de estado
    }

    private ShipmentDto convertToDto(Shipment shipment) {
        ShipmentDto shipmentDto = new ShipmentDto();
        shipmentDto.setId(shipment.getId());
        shipmentDto.setOrderId(shipment.getOrderId());
        shipmentDto.setAddress(shipment.getAddress());
        shipmentDto.setRecipientName(shipment.getRecipientName());
        shipmentDto.setPhoneNumber(shipment.getPhoneNumber());
        shipmentDto.setEmail(shipment.getEmail());
        shipmentDto.setDepartment(shipment.getDepartment());
        shipmentDto.setCity(shipment.getCity());
        shipmentDto.setNeighborhood(shipment.getNeighborhood());
        shipmentDto.setPostalCode(shipment.getPostalCode());
        shipmentDto.setShippingNotes(shipment.getShippingNotes());
        shipmentDto.setCreatedAt(shipment.getCreatedAt());
        shipmentDto.setUpdatedAt(shipment.getUpdatedAt());
        return shipmentDto;
    }

    private Shipment convertToEntity(ShipmentDto shipmentDto) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(shipmentDto.getOrderId());
        shipment.setAddress(shipmentDto.getAddress());
        shipment.setRecipientName(shipmentDto.getRecipientName());
        shipment.setPhoneNumber(shipmentDto.getPhoneNumber());
        shipment.setEmail(shipmentDto.getEmail());
        shipment.setDepartment(shipmentDto.getDepartment());
        shipment.setCity(shipmentDto.getCity());
        shipment.setNeighborhood(shipmentDto.getNeighborhood());
        shipment.setPostalCode(shipmentDto.getPostalCode());
        shipment.setShippingNotes(shipmentDto.getShippingNotes());
        return shipment;
    }
}


