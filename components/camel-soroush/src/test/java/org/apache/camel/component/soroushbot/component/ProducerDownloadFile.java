/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.soroushbot.component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.soroushbot.IOUtils;
import org.apache.camel.component.soroushbot.models.Endpoint;
import org.apache.camel.component.soroushbot.models.MinorType;
import org.apache.camel.component.soroushbot.models.SoroushMessage;
import org.apache.camel.component.soroushbot.support.SoroushBotTestSupport;
import org.apache.camel.component.soroushbot.support.SoroushBotWS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProducerDownloadFile extends SoroushBotTestSupport {

    @EndpointInject("direct:soroush")
    org.apache.camel.Endpoint endpoint;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        SoroushBotWS.clear();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:soroush").to("soroush://" + Endpoint.sendMessage + "/token")
                        .process(exchange -> {
                            SoroushMessage body = exchange.getIn().getBody(SoroushMessage.class);
                            body.setFile((InputStream) null);
                            body.setThumbnail((InputStream) null);
                        })
                        .to("soroush://" + Endpoint.downloadFile + "/token")
                        .to("mock:soroush");
            }
        };
    }

    @Test
    public void checkDownloadFile() throws InterruptedException, IOException {
        SoroushMessage body = new SoroushMessage();
        body.setType(MinorType.TEXT);
        body.setFrom("b1");
        body.setTo("u1");
        String fileContent = "hello";
        String thumbContent = "world";
        body.setFile(new ByteArrayInputStream(fileContent.getBytes()));
        body.setThumbnail(new ByteArrayInputStream(thumbContent.getBytes()));
        context().createProducerTemplate().sendBody(endpoint, body);
        MockEndpoint mockEndpoint = getMockEndpoint("mock:soroush");
        mockEndpoint.setExpectedMessageCount(1);
        mockEndpoint.assertIsSatisfied();
        SoroushMessage mockedMessage = mockEndpoint.getExchanges().get(0).getIn().getBody(SoroushMessage.class);
        Assert.assertEquals("download file successfully", new String(IOUtils.readFully(mockedMessage.getFile(), 1000, false)), fileContent);
        Assert.assertEquals("download thumbnail successfully", new String(IOUtils.readFully(mockedMessage.getThumbnail(), 1000, false)), thumbContent);
    }

}
