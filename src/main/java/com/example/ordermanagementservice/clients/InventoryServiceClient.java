package com.example.ordermanagementservice.clients;

import com.example.ordermanagementservice.dtos.InventoryItemResponseDto;
import com.example.ordermanagementservice.dtos.InventoryReservationRequestDto;
import com.example.ordermanagementservice.dtos.InventoryReservationResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "INVENTORYSERVICE")
public interface InventoryServiceClient {

    @PostMapping("/api/inventory/reservation/reserve")
    InventoryReservationResponseDto reserveInventoryItem(@RequestBody InventoryReservationRequestDto inventoryReservationRequestDto);
}
