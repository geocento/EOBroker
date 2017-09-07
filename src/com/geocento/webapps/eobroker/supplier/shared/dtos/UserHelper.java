package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.User;

/**
 * Created by thomas on 11/07/2016.
 */
public class UserHelper {

    static public UserDTO createUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getUsername());
        userDTO.setIconUrl(user.getUserIcon());
        userDTO.setFullName(user.getFullName());
        userDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(user.getCompany()));
        return userDTO;
    }
}
