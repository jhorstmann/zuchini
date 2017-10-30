package org.zuchini.examples.mockmvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.HeaderResultMatchers;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.annotations.When;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.spring.ScenarioScoped;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
@ScenarioScoped
public class MockMvcSteps {
    @Autowired
    private MockMvc mvc;

    private HttpHeaders requestHeaders = new HttpHeaders();
    private ResultActions resultActions;

    private static MultiValueMap<String, String> toMultiValueMap(Datatable datatable) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (List<String> header : datatable.getRows()) {
            Iterator<String> iterator = header.iterator();
            if (!iterator.hasNext()) {
                throw new IllegalStateException("Name is required in datatable");
            }

            String headerName = iterator.next();
            while (iterator.hasNext()) {
                String headerValue = iterator.next();
                if (!headerValue.isEmpty()) {
                    map.add(headerName, headerValue);
                }
            }
        }
        return map;
    }

    @Given("^the following request headers:$")
    public void givenRequestHeaders(Datatable datatable) {
        requestHeaders.putAll(toMultiValueMap(datatable));
    }

    @When("^a (GET|POST|PUT|DELETE) request to \"([^\"]+)\" is executed$")
    public void whenRequestIsExecuted(String method, String uri) throws Exception {
        resultActions = mvc.perform(request(HttpMethod.valueOf(method), URI.create(uri))
                .headers(requestHeaders));
    }

    @When("^a (GET|POST|PUT|DELETE) request to \"([^\"]+)\" is executed with the following parameters:$")
    public void whenRequestIsExecuted(String method, String uri, Datatable datatable) throws Exception {
        resultActions = mvc.perform(request(HttpMethod.valueOf(method), URI.create(uri))
                .headers(requestHeaders)
                .params(toMultiValueMap(datatable)));
    }

    @Then("^the response status is ([0-9]+)$")
    public void responseStatusIs(int status) throws Exception {
        resultActions.andExpect(status().is(status));
    }

    @Then("^the response body matches the following json paths:$")
    public void responseMatchesJsonPath(Datatable datatable) throws Exception {
        MultiValueMap<String, String> map = toMultiValueMap(datatable);
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            JsonPathResultMatchers jsonPath = jsonPath(key);
            for (String value : entry.getValue()) {
                resultActions.andExpect(jsonPath.value(value));
            }
        }
    }

    @Then("^the response contains the following headers:$")
    public void responseHeadersContain(Datatable datatable) throws Exception {
        MultiValueMap<String, String> map = toMultiValueMap(datatable);
        HeaderResultMatchers header = header();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                resultActions.andExpect(header.string(key, value));
            }
        }
    }

}
