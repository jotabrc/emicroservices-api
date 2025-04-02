package ostro.veda.user_ms.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ostro.veda.user_ms.dto.*;
import ostro.veda.user_ms.response.ResponseBody;
import ostro.veda.user_ms.response.ResponsePayload;
import ostro.veda.user_ms.security.CheckEncryptedHeader;
import ostro.veda.user_ms.service.UserService;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static ostro.veda.user_ms.controller.ControllerDefaults.MAPPING_PREFIX;
import static ostro.veda.user_ms.controller.ControllerDefaults.MAPPING_VERSION_SUFFIX;

@RequestMapping(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/user")
@RestController
@Tag(name = "User Controller", description = "Manage User operations")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponsePayload<UserDto>> add(

            @RequestBody final AddUserDto addUserDto,
            @RequestHeader("X-Secure-Data") String data,
            @RequestHeader("X-Secure-Origin") String header
    ) throws NoSuchAlgorithmException, InvalidKeyException {

        if (!CheckEncryptedHeader.compare(data, header))
            throw new AuthorizationDeniedException("Secure origin authentication failed");

        String uuid = userService.add(addUserDto);
        URI location = ServletUriComponentsBuilder
                .fromPath(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/user/{uuid}")
                .buildAndExpand(uuid)
                .toUri();
        return ResponseEntity.created(location).body(new ResponsePayload<UserDto>()
                .setMessage("User inserted with uuid %s".formatted(uuid)));
    }

    @PostMapping("/add/address")
    public ResponseEntity<ResponsePayload<UserDto>> addAddress(

            @RequestBody final AddUserAddressDto addUserAddressDto,
            @RequestHeader("X-Secure-Data") String data,
            @RequestHeader("X-Secure-Origin") String header
    ) throws NoSuchAlgorithmException {

        String uuid = userService.addAddress(addUserAddressDto);
        URI location = ServletUriComponentsBuilder
                .fromPath(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/address/{uuid}")
                .buildAndExpand(uuid)
                .toUri();
        return ResponseEntity.created(location).body(new ResponsePayload<UserDto>()
                .setMessage("User with uuid %s Address inserted".formatted(uuid)));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponsePayload<UserDto>> update(

            @RequestBody final UpdateUserDto updateUserDto,
            @RequestHeader("X-Secure-Data") String data,
            @RequestHeader("X-Secure-Origin") String header
    ) throws NoSuchAlgorithmException {

        userService.update(updateUserDto);
        return ResponseEntity.ok(new ResponsePayload<UserDto>()
                .setMessage("User with uuid %s has been updated".formatted(updateUserDto.getUuid())));
    }

    @PutMapping("/update/password")
    public ResponseEntity<ResponsePayload<UserDto>> updatePassword(

            @RequestBody final UpdateUserPasswordDto updateUserPasswordDto,
            @RequestHeader("X-Secure-Data") String data,
            @RequestHeader("X-Secure-Origin") String header
    ) throws NoSuchAlgorithmException {

        userService.updatePassword(updateUserPasswordDto);
        return ResponseEntity.ok(new ResponsePayload<UserDto>()
                .setMessage("User with uuid %s password updated".formatted(updateUserPasswordDto.getUuid())));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ResponsePayload<UserDto>> getUserByUuid(
            @PathVariable("uuid") final String uuid,
            @RequestHeader("X-Secure-Data") String data,
            @RequestHeader("X-Secure-Origin") String header
    ) throws NoSuchAlgorithmException {

        UserDto userDto = userService.getUserByUuid(uuid);
        return ResponseEntity.ok(new ResponsePayload<UserDto>()
                .setMessage("User with uuid %s found".formatted(uuid))
                .setBody(new ResponseBody<UserDto>()
                        .setData(List.of(userDto))));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponsePayload<UserSessionDto>> login(
            @RequestBody final LoginDto loginDto,
            @RequestHeader("X-Secure-Data") String data,
            @RequestHeader("X-Secure-Origin") String header
    ) throws NoSuchAlgorithmException, AuthException {

        UserSessionDto userSessionDto = userService.login(loginDto);
        return ResponseEntity.ok(new ResponsePayload<UserSessionDto>()
                .setMessage("User with username %s found".formatted(loginDto.getUsername()))
                .setBody(new ResponseBody<UserSessionDto>()
                        .setData(List.of(userSessionDto))));
    }
}
