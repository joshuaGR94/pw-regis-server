/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tss.pw.security;

import it.tss.pw.users.User;
import it.tss.pw.users.UserStore;
import java.math.BigDecimal;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author joshua
 */
@Path("/authentication")
@PermitAll
public class AuthenticationResource {

    @Inject
    UserStore store;

    @Inject
    JWTManager jwtManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Credential credential) {
        Optional<User> user = store.search(credential);
        if (user.isPresent()) {
            JsonObject jwt = Json.createObjectBuilder()
                    .add("token", token(user.get()))
                    .build();
            return Response.ok(jwt)
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .header("reason", "invalid username or password")
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("usr") String usr, @FormParam("pwd") String pwd) {
        Optional<User> user = store.search(new Credential(usr, pwd));
        if (user.isPresent()) {
            JsonObject jwt = Json.createObjectBuilder()
                    .add("token", token(user.get()))
                    .build();
            return Response.ok(jwt)
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .header("token", "ko")
                .build();
    }

    private String token(User usr) {
        String result = jwtManager.generate(usr);
        System.out.println("------------ generated token -------------------");
        System.out.println(result);
        System.out.println("------------ curl command for test -------------");
        System.out.println("curl -v -i -H'Authorization: Bearer " + result + "' http://localhost:8080/pw-regis/resources/users");
        return result;
    }
}
