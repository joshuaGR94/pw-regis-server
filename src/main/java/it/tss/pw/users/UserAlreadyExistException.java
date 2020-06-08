/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tss.pw.users;

import javax.ejb.EJBException;

/**
 *
 * @author joshua
 */
public class UserAlreadyExistException extends EJBException {

    private final String username;

    public UserAlreadyExistException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    
}
