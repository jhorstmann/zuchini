package org.zuchini.examples.mockmvc.provider;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/hello")
public class HelloResource {
    public static class Result {
        private final String name;
        private final String greeting;

        public Result(String name, String greeting) {
            this.name = name;
            this.greeting = greeting;
        }

        public String getName() {
            return name;
        }

        public String getGreeting() {
            return greeting;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Result hello(@RequestParam("name") String name) {
        return new Result(name, "Hello " + name);
    }
}
