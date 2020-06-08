/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tss.pw.users;

import it.tss.pw.security.Credential;
import it.tss.pw.security.SecurityEncoding;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author joshua
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserStore {

    @PersistenceContext(name = "pw")
    EntityManager em;

    @PostConstruct
    public void init() {

    }

    public Collection<User> all() {
        return em.createNamedQuery(User.FIND_ALL)
                .getResultList();
    }

    public Optional<User> find(Long id) {
        User found = em.find(User.class, id);
        return found == null ? Optional.empty() : Optional.of(found);
    }

    public User create(User u) {
        System.out.println("----------------------" + u + " ----------------------------------");
        if (findByUsr(u.getUsr()).isPresent()) {
            throw new UserAlreadyExistException(u.getUsr());
        }
        u.setPwd(SecurityEncoding.shaHash(u.getPwd()));
        return em.merge(u);
    }

    public User update(User u) {
        return em.merge(u);
    }

    public void delete(Long id) {
        em.remove(em.find(User.class, id));
    }

    public Optional<User> findByUsr(String usr) {
        return em.createNamedQuery(User.FIND_BY_USR, User.class)
                .setParameter("usr", usr)
                .getResultStream()
                .findFirst();
    }

    public Collection<User> search(String search) {
        return em.createNamedQuery(User.SEARCH)
                .setParameter("fname", "%" + search + "%")
                .setParameter("lname", "%" + search + "%")
                .setParameter("usr", "%" + search + "%")
                .getResultList();
    }

    public Optional<User> search(Credential credential) {
        credential.setPwd(SecurityEncoding.shaHash(credential.getPwd()));
        try {
            User found = em.createNamedQuery(User.FIND_BY_USR_PWD, User.class)
                    .setParameter("usr", credential.getUsr())
                    .setParameter("pwd", credential.getPwd())
                    .getSingleResult();
            return Optional.of(found);
        } catch (NoResultException ex) {
            Logger.getLogger(UserStore.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
}
