package com.alek.service;

import com.alek.model.User;

public interface UserService {

     User findUserByJwtToken(String jwt) throws Exception;
     User findUserByEmail(String email) throws Exception;

}
