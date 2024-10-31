package com.misakguambshop.app.service;


import com.misakguambshop.app.dto.ShipmentDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.Shipment;
import com.misakguambshop.app.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public List<ShipmentDto> getAllShipments() {
        return shipmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ShipmentDto getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con ID: " + id));
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
        validateShipment(shipmentDto);
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con ID: " + id));

        updateShipmentFields(existingShipment, shipmentDto);
        Shipment updatedShipment = shipmentRepository.save(existingShipment);
        return convertToDto(updatedShipment);
    }

    @Transactional
    public ShipmentDto partialUpdateShipment(Long id, Map<String, Object> updates) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con ID: " + id));

        if (updates.containsKey("address")) {
            existingShipment.setAddress((String) updates.get("address"));
        }
        if (updates.containsKey("recipientName")) {
            existingShipment.setRecipientName((String) updates.get("recipientName"));
        }
        if (updates.containsKey("phoneNumber")) {
            existingShipment.setPhoneNumber((String) updates.get("phoneNumber"));
        }
        if (updates.containsKey("email")) {
            existingShipment.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("department")) {
            existingShipment.setDepartment((String) updates.get("department"));
        }
        if (updates.containsKey("city")) {
            existingShipment.setCity((String) updates.get("city"));
        }
        if (updates.containsKey("neighborhood")) {
            existingShipment.setNeighborhood((String) updates.get("neighborhood"));
        }
        if (updates.containsKey("postalCode")) {
            existingShipment.setPostalCode((String) updates.get("postalCode"));
        }
        if (updates.containsKey("shippingNotes")) {
            existingShipment.setShippingNotes((String) updates.get("shippingNotes"));
        }

        return convertToDto(shipmentRepository.save(existingShipment));
    }

    @Transactional
    public void deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Envío no encontrado con ID: " + id);
        }
        shipmentRepository.deleteById(id);
    }

    private void validateShipment(ShipmentDto shipmentDto) {
        if (shipmentDto == null) {
            throw new IllegalArgumentException("El envío no puede ser nulo");
        }

        if (shipmentDto.getAddress() != null && shipmentDto.getAddress().length() < 5) {
            throw new IllegalArgumentException("La dirección debe tener al menos 5 caracteres");
        }

        if (shipmentDto.getPhoneNumber() != null && !shipmentDto.getPhoneNumber().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("El número de teléfono debe tener 10 dígitos");
        }
    }

    private Shipment convertToEntity(ShipmentDto dto) {
        if (dto == null) {
            return null;
        }

        Shipment shipment = new Shipment();
        shipment.setAddress(dto.getAddress());
        shipment.setRecipientName(dto.getRecipientName());
        shipment.setPhoneNumber(dto.getPhoneNumber());
        shipment.setEmail(dto.getEmail());
        shipment.setDepartment(dto.getDepartment());
        shipment.setCity(dto.getCity());
        shipment.setNeighborhood(dto.getNeighborhood());
        shipment.setPostalCode(dto.getPostalCode());
        shipment.setShippingNotes(dto.getShippingNotes());

        return shipment;
    }

    private void updateShipmentFields(Shipment shipment, ShipmentDto dto) {
        shipment.setAddress(dto.getAddress());
        shipment.setRecipientName(dto.getRecipientName());
        shipment.setPhoneNumber(dto.getPhoneNumber());
        shipment.setEmail(dto.getEmail());
        shipment.setDepartment(dto.getDepartment());
        shipment.setCity(dto.getCity());
        shipment.setNeighborhood(dto.getNeighborhood());
        shipment.setPostalCode(dto.getPostalCode());
        shipment.setShippingNotes(dto.getShippingNotes());
    }

    private ShipmentDto convertToDto(Shipment shipment) {
        if (shipment == null) {
            return null;
        }

        ShipmentDto dto = new ShipmentDto();
        dto.setId(shipment.getId());
        dto.setAddress(shipment.getAddress());
        dto.setRecipientName(shipment.getRecipientName());
        dto.setPhoneNumber(shipment.getPhoneNumber());
        dto.setEmail(shipment.getEmail());
        dto.setDepartment(shipment.getDepartment());
        dto.setCity(shipment.getCity());
        dto.setNeighborhood(shipment.getNeighborhood());
        dto.setPostalCode(shipment.getPostalCode());
        dto.setShippingNotes(shipment.getShippingNotes());
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setUpdatedAt(shipment.getUpdatedAt());
        return dto;
    }
}


