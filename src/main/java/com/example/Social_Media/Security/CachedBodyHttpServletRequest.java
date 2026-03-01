package com.example.Social_Media.Security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;

/**
 * A request wrapper that reads and caches the body immediately on construction,
 * so it can be read multiple times (once by JwtAuthenticationFilter to extract
 * the "action" field, and again by the controller).
 *
 * Unlike ContentCachingRequestWrapper, this caches EAGERLY — the body is
 * available via getBody() before anyone has called getInputStream().
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // Read and cache the body right now, before any filter touches the stream.
        this.cachedBody = request.getInputStream().readAllBytes();
    }

    /** Returns the raw cached body bytes — safe to call multiple times. */
    public byte[] getBody() {
        return cachedBody;
    }

    /** Every call returns a fresh stream over the same cached bytes. */
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override public int read() { return byteArrayInputStream.read(); }
            @Override public boolean isFinished() { return byteArrayInputStream.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(ReadListener listener) {}
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}