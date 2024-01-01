package com.prt.bookstore.auth.payload.request;

import jakarta.validation.constraints.NotBlank;


public class DeleteRequest {
    @NotBlank
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
