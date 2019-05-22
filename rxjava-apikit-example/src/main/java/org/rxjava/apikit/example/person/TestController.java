package org.rxjava.apikit.example.person;

import org.rxjava.apikit.core.Login;
import org.rxjava.apikit.example.form.TestForm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author happy
 */
@RestController
@RequestMapping("person")
public class TestController {

    /**
     * 路径变量测试
     */
    @Login(false)
    @GetMapping("testeeee/{id}")
    public Mono<Integer> testewwPath(
            @PathVariable String id,
            @Valid TestForm form
    ) {
        return Mono.empty();
    }
}
