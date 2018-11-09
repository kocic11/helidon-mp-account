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

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.helidon.microprofile.server.Server;

class MainTest {
        private static Server server;

        public static void main(String[] args) throws Exception {
                MainTest test = new MainTest();
                startTheServer();
                test.add();  
        }

        @BeforeAll
        public static void startTheServer() throws Exception {
                server = Main.startServer();
        }

        @Test
        void add() {

                Account account = new Account("premium", "0001");
                Client client = ClientBuilder.newClient();

                Response response = client.target(getConnectionString("/account/add")).request()
                                .put(Entity.json(account));
                Assertions.assertEquals(Response.Status.CREATED.toString(), response.getStatusInfo().toString(),
                                "PUT status code");
        }
        
        @AfterAll
        static void destroyClass() {
                CDI<Object> current = CDI.current();
                ((SeContainer) current).close();
        }

        private String getConnectionString(String path) {
                return "http://localhost:" + server.getPort() + path;
        }
}
