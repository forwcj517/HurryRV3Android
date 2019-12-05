package com.hurry.custom.controller;

/**
 * Created by Administrator on 6/23/2018.
 */


import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

class MyRedirectHandler extends DefaultRedirectHandler {
    private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    private final boolean enableRedirects;

    public MyRedirectHandler(boolean allowRedirects) {
        this.enableRedirects = allowRedirects;
    }

    public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
        if(!this.enableRedirects) {
            return false;
        } else if(response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        } else {
            int statusCode = response.getStatusLine().getStatusCode();
            switch(statusCode) {
                case 301:
                case 302:
                case 303:
                case 307:
                    return true;
                case 304:
                case 305:
                case 306:
                default:
                    return false;
            }
        }
    }

    public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
        if(response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        } else {
            Header locationHeader = response.getFirstHeader("location");
            if(locationHeader == null) {
                throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
            } else {
                String location = locationHeader.getValue().replaceAll(" ", "%20");

                URI uri;
                try {
                    uri = new URI(location);
                } catch (URISyntaxException var13) {
                    throw new ProtocolException("Invalid redirect URI: " + location, var13);
                }

                HttpParams params = response.getParams();
                if(!uri.isAbsolute()) {
                    if(params.isParameterTrue("http.protocol.reject-relative-redirect")) {
                        throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
                    }

                    HttpHost target = (HttpHost)context.getAttribute("http.target_host");
                    if(target == null) {
                        throw new IllegalStateException("Target host not available in the HTTP context");
                    }

                    HttpRequest request = (HttpRequest)context.getAttribute("http.request");

                    try {
                        URI requestURI = new URI(request.getRequestLine().getUri());
                        URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, true);
                        uri = URIUtils.resolve(absoluteRequestURI, uri);
                    } catch (URISyntaxException var12) {
                        throw new ProtocolException(var12.getMessage(), var12);
                    }
                }

                if(params.isParameterFalse("http.protocol.allow-circular-redirects")) {
                    RedirectLocations redirectLocations = (RedirectLocations)context.getAttribute("http.protocol.redirect-locations");
                    if(redirectLocations == null) {
                        redirectLocations = new RedirectLocations();
                        context.setAttribute("http.protocol.redirect-locations", redirectLocations);
                    }

                    URI redirectURI;
                    if(uri.getFragment() != null) {
                        try {
                            HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
                            redirectURI = URIUtils.rewriteURI(uri, target, true);
                        } catch (URISyntaxException var11) {
                            throw new ProtocolException(var11.getMessage(), var11);
                        }
                    } else {
                        redirectURI = uri;
                    }

                    if(redirectLocations.contains(redirectURI)) {
                        throw new CircularRedirectException("Circular redirect to '" + redirectURI + "'");
                    }

                    redirectLocations.add(redirectURI);
                }

                return uri;
            }
        }
    }
}
