package im.turms.service.external.access;

import im.turms.server.common.access.admin.dto.response.HttpHandlerResult;
import im.turms.server.common.access.admin.dto.response.ResponseDTO;
import im.turms.server.common.access.admin.permission.RequiredPermission;
import im.turms.server.common.access.admin.web.annotation.PostMapping;
import im.turms.server.common.access.admin.web.annotation.RequestBody;
import im.turms.server.common.access.admin.web.annotation.RestController;
import im.turms.server.common.access.common.ResponseStatusCode;
import im.turms.server.common.domain.user.po.User;
import im.turms.server.common.infra.exception.ResponseException;
import im.turms.service.domain.user.access.admin.dto.request.AddUserDTO;
import im.turms.service.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import static im.turms.server.common.access.admin.permission.AdminPermission.GROUP_CREATE;
import static im.turms.server.common.access.admin.permission.AdminPermission.USER_CREATE;

/**
 * @Author : nadir
 * @create 2023/2/3 16:07
 */
@RestController("external")
public class ExternalController {

    @Autowired
    UserService userService;

    @PostMapping("accountImport")
    @RequiredPermission(USER_CREATE)
    public Mono<HttpHandlerResult<ResponseDTO<User>>> accountImport(@RequestBody AddUserDTO addUserDTO) {
        return userService.checkIfUserExists(addUserDTO.id(), true)
                .flatMap(op -> {
                    if (op) return Mono.error(new Exception("user is exist"));
                    Mono<User> addUser = userService.addUser(
                            addUserDTO.id(),
                            addUserDTO.password(),
                            addUserDTO.name(),
                            addUserDTO.intro(),
                            addUserDTO.profilePicture(),
                            addUserDTO.profileAccessStrategy(),
                            addUserDTO.permissionGroupId(),
                            addUserDTO.registrationDate(),
                            addUserDTO.isActive());
                    return HttpHandlerResult.okIfTruthy(addUser);
                });
    }

    @PostMapping("group/create")
    @RequiredPermission(GROUP_CREATE)
    public Mono<HttpHandlerResult<ResponseDTO<User>>> groupCreate(@RequestBody AddUserDTO addUserDTO) {
        Mono<User> addUser = userService.addUser(
                addUserDTO.id(),
                addUserDTO.password(),
                addUserDTO.name(),
                addUserDTO.intro(),
                addUserDTO.profilePicture(),
                addUserDTO.profileAccessStrategy(),
                addUserDTO.permissionGroupId(),
                addUserDTO.registrationDate(),
                addUserDTO.isActive());
        return HttpHandlerResult.okIfTruthy(addUser);
    }
}
