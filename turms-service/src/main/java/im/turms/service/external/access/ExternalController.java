package im.turms.service.external.access;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;
import im.turms.server.common.access.admin.dto.response.HttpHandlerResult;
import im.turms.server.common.access.admin.dto.response.ResponseDTO;
import im.turms.server.common.access.admin.permission.RequiredPermission;
import im.turms.server.common.access.admin.web.annotation.PostMapping;
import im.turms.server.common.access.admin.web.annotation.QueryParam;
import im.turms.server.common.access.admin.web.annotation.RequestBody;
import im.turms.server.common.access.admin.web.annotation.RestController;
import im.turms.server.common.access.client.dto.constant.GroupMemberRole;
import im.turms.server.common.domain.user.po.User;
import im.turms.server.common.infra.net.InetAddressUtil;
import im.turms.service.domain.group.access.admin.dto.request.AddGroupDTO;
import im.turms.service.domain.group.po.Group;
import im.turms.service.domain.group.service.GroupMemberService;
import im.turms.service.domain.group.service.GroupService;
import im.turms.service.domain.message.access.admin.dto.request.CreateMessageDTO;
import im.turms.service.domain.message.service.MessageService;
import im.turms.service.domain.user.access.admin.dto.request.AddUserDTO;
import im.turms.service.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

import static im.turms.server.common.access.admin.permission.AdminPermission.*;

/**
 * @Author : nadir
 * @create 2023/2/3 16:07
 */
@RestController("external")
public class ExternalController {

    @Autowired
    UserService userService;
    @Autowired
    GroupService groupService;
    @Autowired
    GroupMemberService groupMemberService;
    @Autowired
    MessageService messageService;

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
    public Mono<HttpHandlerResult<ResponseDTO<Object>>> addGroup(@RequestBody AddGroupDTO addGroupDTO) {
        Long ownerId = addGroupDTO.ownerId();
        Mono<Group> createdGroup = groupService.authAndCreateGroup(
                addGroupDTO.creatorId(),
                ownerId == null ? addGroupDTO.creatorId() : ownerId,
                addGroupDTO.name(),
                addGroupDTO.intro(),
                addGroupDTO.announcement(),
                addGroupDTO.minimumScore(),
                addGroupDTO.typeId(),
                addGroupDTO.creationDate(),
                addGroupDTO.deletionDate(),
                addGroupDTO.muteEndDate(),
                addGroupDTO.isActive());
        //todo nadir groupId 的问题
        final String[] groupId = new String[1];
        return createdGroup.flatMap(op -> {
                    groupId[0] = op.getId() + "";
                    return groupMemberService.addGroupMembers(
                                    op.getId(),
                                    addGroupDTO.members(),
                                    GroupMemberRole.MEMBER, null, null, null)
                            .thenReturn(HttpHandlerResult.okIfTruthy(groupId[0]));
                }
        );
    }

    @PostMapping("message/send")
    @RequiredPermission(MESSAGE_CREATE)
    public Mono<HttpHandlerResult<ResponseDTO<Void>>> createMessages(
            @QueryParam(defaultValue = "true") boolean send,
            @RequestBody CreateMessageDTO createMessageDTO) {
        String ip = createMessageDTO.senderIp();
        Mono<Void> sendMono = messageService.authAndSaveAndSendMessage(
                send,
                createMessageDTO.senderId(),
                createMessageDTO.senderDeviceType(),
                ip == null ? null : InetAddressUtil.ipStringToBytes(ip),
                createMessageDTO.id(),
                createMessageDTO.isGroupMessage(),
                createMessageDTO.isSystemMessage(),
                createMessageDTO.text(),
                createMessageDTO.records(),
                createMessageDTO.targetId(),
                createMessageDTO.burnAfter(),
                createMessageDTO.referenceId(),
                createMessageDTO.preMessageId());
        return sendMono.thenReturn(HttpHandlerResult.RESPONSE_OK);
    }
}
