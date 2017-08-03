package com.geocento.webapps.eobroker.customer.shared.utils;

import com.geocento.webapps.eobroker.customer.shared.CreateUserDTO;

/**
 * Created by thomas on 03/08/2017.
 */
public class UserHelper {

    public static String usernameRegexp = "^([A-Za-z]|[0-9]|_)+$";

    public static String phoneRegexp = "^(\\(?\\+?[0-9]*\\)?)?[0-9_\\- \\(\\)]*$";

    public static String emailregExp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    public static void checkUserValues(CreateUserDTO createUserDTO) throws Exception {
        if(createUserDTO.getUserName() == null ||
                createUserDTO.getUserName().length() < 3) {
            throw new Exception("The user name is too short, 3 characters minimum");
        }

        if(!createUserDTO.getUserName().toLowerCase().matches(usernameRegexp)) {
            throw new Exception("The user name is not valid, only alpha numerical and underscore characters are allowed");
        }

        if(createUserDTO.getUserPassword() == null || createUserDTO.getUserPassword().length() < 5) {
            throw new Exception("The password is not valid");
        }

        if(createUserDTO.getEmail() == null ||
                createUserDTO.getEmail().length() < 3 ||
                !createUserDTO.getEmail().toLowerCase().matches(emailregExp)) {
            throw new Exception("The email is not valid");
        }

        if(createUserDTO.getFullName() == null || createUserDTO.getFullName().length() < 3) {
            throw new Exception("The full name is not valid");
        }

        if(createUserDTO.getPhoneNumber() == null ||
                createUserDTO.getPhoneNumber().length() < 3 ||
                !createUserDTO.getPhoneNumber().toLowerCase().matches(phoneRegexp)) {
            throw new Exception("The phone number is not valid");
        }
    }
}
