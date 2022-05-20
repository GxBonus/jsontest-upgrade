package com.example.jsontestupgrade;

import java.util.Map;
import java.util.Objects;

public class Headers {
    Map<String, String> headers;

    public Headers(Map<String, String> headers){
        this.headers = headers;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Headers headers1 = (Headers) o;
        return Objects.equals(headers, headers1.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
