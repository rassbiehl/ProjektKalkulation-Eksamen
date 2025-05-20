package com.example.projektkalkulationeksamen.validation;

import com.example.projektkalkulationeksamen.dto.ProjectDTO;
import com.example.projektkalkulationeksamen.model.Role;

public class AccessValidation {

    public static boolean isOwner (int userId, Role userRole, ProjectDTO projectDTO) {
        return userRole == Role.PROJECTMANAGER && projectDTO.getProjectManagerId() == userId;
    }

}
