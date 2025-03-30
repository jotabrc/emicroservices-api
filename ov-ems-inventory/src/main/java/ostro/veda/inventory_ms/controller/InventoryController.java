package ostro.veda.inventory_ms.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ostro.veda.inventory_ms.dto.AddInventoryDto;
import ostro.veda.inventory_ms.dto.InventoryDto;
import ostro.veda.inventory_ms.response.ResponsePayload;
import ostro.veda.inventory_ms.service.InventoryService;

import java.net.URI;

import static ostro.veda.inventory_ms.controller.ControllerDefaults.MAPPING_PREFIX;
import static ostro.veda.inventory_ms.controller.ControllerDefaults.MAPPING_VERSION_SUFFIX;

@RequestMapping(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/inventory")
@RestController
@Tag(name = "Inventory Controller", description = "Creates Inventory unit")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponsePayload<InventoryDto>> addInventory(@RequestBody final AddInventoryDto addInventoryDto) {
        String uuid = inventoryService.addInventory(addInventoryDto);
        URI location = ServletUriComponentsBuilder
                .fromPath(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/inventory/{uuid}")
                .buildAndExpand(uuid)
                .toUri();
        return ResponseEntity.created(location).body(new ResponsePayload<InventoryDto>()
                .setMessage("Inventory inserted with uuid %s".formatted(uuid)));
    }
}
