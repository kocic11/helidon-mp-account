/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package poc.mp.account;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/account")
@RequestScoped
public class AccountResource {

    @Inject
    private AccountRepo accountRepo;

    /**
     * Return list of accounts matching the provided type.
     * 
     * @param type account type
     * @return {@link JsonArray}
     */
    @Path("/{type}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getByName(@PathParam("type") final String type) {
        List<Account> accounts = accountRepo.getByType(type);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        accounts.forEach(account -> {
            JsonObject jsonObject = Json.createObjectBuilder().add("id", account.getId()).add("type", account.getType())
                    .add("userId", account.getUserId()).build();
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        Account account = accountRepo.getById(id);
        return Response.ok(account).build();
    }

    /**
     * Add an account.
     * 
     * @param jsonObject the account
     * @return {@link Response}
     */
    @Path("/add")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(JsonObject jsonObject) {
        Account account = new Account();
        account.setType(getStringFromJson("type", jsonObject));
        String userId = getStringFromJson("userId", jsonObject);
        account.setUserId(userId);
        if (checkUserId(userId)) {
            String id = accountRepo.add(account);
            return Response.created(UriBuilder.fromResource(this.getClass()).path("id/" + id).build()).build();
        } else {
            return Response.noContent().build();
        }
    }

    private boolean checkUserId(String userId) {
        if (userId == null) {
            return false;
        }

        Client client = ClientBuilder.newClient();
        JsonObject jsonObject = client.target(getConnectionString("/user/id/" + userId)).request()
                .get(JsonObject.class);
        if (jsonObject != null) {
            if (userId.equals(getStringFromJson("id", jsonObject))) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    private String getStringFromJson(String key, JsonObject json) {
        String returnedString = null;
        if (json.containsKey(key)) {
            JsonString value = json.getJsonString(key);
            if (value != null) {
                returnedString = value.getString();
            }
        }
        return returnedString;
    }

    private String getConnectionString(String path) {
        String userHost = System.getenv("USER_PORT");
        if (userHost != null) {
            return "http://user:8080" + path;
        }

        return "http://localhost:8080" + path;
    }
}
