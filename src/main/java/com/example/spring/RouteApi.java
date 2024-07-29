package com.example.spring;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RouteApi extends RouteBuilder {
    @Override
    public void configure() throws Exception {
//        onException(Exception.class)
//                .log("This is error message: ${exception.message}");
//        log.info("Starting route configuration");
        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);

        rest("/rest")
                .get("")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .routeId("Get")
                .to("direct:getAll")
                .get("/{id}")
                .to("direct:getById")
                .post("")
                .to("direct:createUser")
                .put("/{id}")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .routeId("UpdateUser")
                .to("direct:updateUser")
                .delete("/{id}")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .routeId("DeleteUser")
                .to("direct:deleteUser");

        // Route Get All
        from("direct:getAll")
//                .bean(UserRepository.class, "getAllUsers");
                .doTry()
                .log("Fetching all users")
                .bean(UserRepository.class, "getAllUsers()")
                .log("All users fetched: ${body}")
                .doCatch(Exception.class)
                .log("This is error message: ${exception.message}")
                .setBody(simple("{\"error\": \"Failed to fetch users\"}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
        //Route Get By ID
        from("direct:getById")
//                .log("Received Header: ${header.id}")
//                .bean(UserRepository.class , "getById(${header.id})")
//                .log("Fetched user: ${body}")
//                .onException(Exception.class)
//                .handled(true)
//                .log("Error: ${exception.message}")
//                .setBody(simple("Error: ${exception.message}"))
//                .end();

                .doTry()
                .log("Received Header: ${header.id}")
                .bean(UserRepository.class, "getById(${header.id})")
                .log("User fetched: ${body}")
                .doCatch(Exception.class)
                .log("This is error message: ${exception.message}")
                .setBody(simple("{\"error\": \"User not found\"}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));

        //Route Post Data
        from("direct:createUser")
                .doTry()
                .log("Creating user with body: ${body}")
                .marshal().json()
                .unmarshal().json(User.class)
                .bean(UserRepository.class, "createUser(${body})")
                .log("User created: ${body}")
                .doCatch(Exception.class)
                .log("This is error message: ${exception.message}")
                .setBody(simple("{\"error\": \"Failed to create user\"}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
        //Route Put Data
        from("direct:updateUser")
                .doTry()
                .log("Updating user with ID: ${header.id} and body: ${body}")
                .marshal().json()
                .unmarshal().json(User.class)
                .bean(UserRepository.class, "updateUser(${header.id}, ${body})")
                .log("User updated: ${body}")
                .doCatch(Exception.class)
                .log("This is error message: ${exception.message}")
                .setBody(simple("{\"error\": \"Failed to update user\"}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));

        //Route Delete Data
        from("direct:deleteUser")
                .doTry()
                .log("Deleting user with ID: ${header.id}")
                .bean(UserRepository.class, "deleteUser(${header.id})")
                .log("User deleted T: ${body}")
                .doCatch(Exception.class)
                .log("This is error message: ${exception.message}")
                .setBody(simple("{\"error\": \"Failed to delete user\"}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));

//        log.info("Finished route configuration");
    }
}
