/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.service.external.dto;

import im.turms.server.common.access.client.dto.constant.ProfileAccessStrategy;
import im.turms.server.common.domain.common.access.dto.ControllerDTO;
import im.turms.server.common.infra.security.SecurityValueConst;

import java.util.Date;

/**
 * @author James Chen
 */
public record UserLoginDTO(
        Long id,
//        @SensitiveProperty
        String password,
        String name,
        String intro,
        String profilePicture,
        ProfileAccessStrategy profileAccessStrategy,
        Long permissionGroupId,
        Date registrationDate,
        Boolean isActive
) implements ControllerDTO {
    @Override
    public String toString() {
        return "AddUserDTO[" +
                "id=" + id +
                ", password=" + SecurityValueConst.SENSITIVE_VALUE +
                ", name=" + name +
                ", intro=" + intro +
                ", profilePicture=" + profilePicture +
                ", profileAccessStrategy=" + profileAccessStrategy +
                ", permissionGroupId=" + permissionGroupId +
                ", registrationDate=" + registrationDate +
                ", isActive=" + isActive +
                ']';
    }
}
