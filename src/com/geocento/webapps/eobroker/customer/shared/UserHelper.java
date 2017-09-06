package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;

/**
 * Created by thomas on 11/07/2016.
 */
public class UserHelper {

    static public UserDTO createUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getUsername());
        userDTO.setFullName(user.getFullName());
        userDTO.setIconURL(user.getUserIcon());
        userDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(user.getCompany()));
        userDTO.setCreationDate(user.getCreationDate());
        return userDTO;
    }
}
