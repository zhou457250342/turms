/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.server.common.access.admin.web;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import reactor.netty.http.server.HttpServerRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author James Chen
 */
public class HttpUtil {

    private HttpUtil() {
    }

    public static void allowAnyRequest(HttpHeaders headers) {
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "*")
                .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*")
                .set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "7200");
    }

    public static boolean isPreFlightRequest(HttpServerRequest request) {
        HttpHeaders headers = request.requestHeaders();
        return request.method() == HttpMethod.OPTIONS
                && headers.contains(HttpHeaderNames.ORIGIN)
                && headers.contains(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
    }

    public static boolean isServerError(HttpResponseStatus status) {
        return status.code() >= 500;
    }

    public static String doPostJson(String url, String json, String token, int timeout, Map<String, String> headers) throws Exception {
        try {
            var httpClient = HttpClients.createDefault();
            var response = new StringBuffer();
            var httpPost = new HttpPost(url);
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).build());
            httpPost.addHeader("User-Agent", "Mozilla/5.0");
            httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
            httpPost.addHeader("Authorization", "Token " + token);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpPost.addHeader(key, headers.get(key));
                }
            }
            StringEntity stringEntity = new StringEntity(json, StandardCharsets.UTF_8);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            var httpResponse = httpClient.execute(httpPost);
            var reader = new BufferedReader(new InputStreamReader(
                    httpResponse.getEntity().getContent()));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
