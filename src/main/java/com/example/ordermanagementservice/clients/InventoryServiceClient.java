package com.example.ordermanagementservice.clients;

import com.example.ordermanagementservice.dtos.InventoryReservationRequestDto;
import com.example.ordermanagementservice.dtos.InventoryReservationResponseDto;
import com.example.ordermanagementservice.dtos.RevokeInventoryReservationDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "INVENTORYSERVICE")
public interface InventoryServiceClient {

    @PostMapping("/api/inventory/reservation/reserve")
    InventoryReservationResponseDto reserveInventoryItem(@RequestBody InventoryReservationRequestDto inventoryReservationRequestDto);

    @PostMapping("/api/inventory/reservation/revoke")
    InventoryReservationResponseDto revokeReservation(@RequestBody @Valid RevokeInventoryReservationDto revokeReservationDto);
}
