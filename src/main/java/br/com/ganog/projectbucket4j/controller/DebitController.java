package br.com.ganog.projectbucket4j.controller;

import br.com.ganog.projectbucket4j.controller.request.DebitRequest;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

@RestController
@RequiredArgsConstructor
public class DebitController {

    private final Supplier<BucketConfiguration> bucketConfiguration;

    private final ProxyManager<String> proxyManager;

    @PostMapping("/debit")
    String debit(@RequestBody DebitRequest debitRequest) {

        final var bucket = proxyManager.builder().build(String.format("%s_%s", debitRequest.account(), debitRequest.agency()), bucketConfiguration);

        if (bucket.tryConsumeAndReturnRemaining(1).isConsumed()) {
            return "Debit operation successful";
        } else {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Debit operation failed");
        }
    }
}
