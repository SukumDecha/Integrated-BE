package sit.int202.ecommerce.modules.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.ecommerce.modules.user.dto.request.UserUpdateRequest;
import sit.int202.ecommerce.modules.user.service.UserService;

@Tag(name = "User", description = "APIs for user management")
@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class UserV2Controller {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest user) {
        return ResponseEntity.ok(userService.updateById(user, id));
    }

}
