package br.com.ganog.projectbucket4j.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DebitRequest (@JsonProperty("agency") String agency, @JsonProperty("account") String account) { }
