package com.kosuri.stores.model.request;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

@Setter
@Getter
@ToString
public class AddTaskRequest extends RequestEntity<AddTaskRequest> {
    public AddTaskRequest(HttpMethod method, URI url) {
        super(method, url);
    }

    @Nonnull
    private int taskId;
    @Nonnull
    private String taskName;

}
