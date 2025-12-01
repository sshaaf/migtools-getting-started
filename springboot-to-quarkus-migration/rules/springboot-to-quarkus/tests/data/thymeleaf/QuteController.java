package com.example.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class QuteController {
    
    @Inject
    Template usersList; // users/list.html template
    
    @Inject
    Template userDetail; // users/detail.html template
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance listUsers() {
        // Migrated from Thymeleaf - no Model needed
        return usersList
            .data("users", userService.findAll())
            .data("title", "User List");
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showUser(@PathParam("id") Long id) {
        return userDetail.data("user", userService.findById(id));
    }
}








