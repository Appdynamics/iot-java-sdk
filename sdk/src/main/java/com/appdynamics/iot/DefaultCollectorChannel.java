/**
 * Copyright (c) 2018 AppDynamics LLC and its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appdynamics.iot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class DefaultCollectorChannel extends CollectorChannel {
    private HttpURLConnection connection;

    private synchronized HttpURLConnection getConnection() throws IOException {
        if (connection == null) {
            connection = (HttpURLConnection) getURL().openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(getReadTimeout());
            connection.setConnectTimeout(getConnectTimeout());
            connection.setRequestMethod(getRequestMethod());
            Map<String, List<String>> requestProperties = getRequestProperties();
            for (Map.Entry<String, List<String>> entry : requestProperties.entrySet()) {
                for (String value : entry.getValue()) {
                    connection.addRequestProperty(entry.getKey(), value);
                }
            }
        }
        return connection;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return getConnection().getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getConnection().getInputStream();
    }

    @Override
    public InputStream getErrorStream() throws IOException {
        return getConnection().getErrorStream();
    }

    @Override
    public int getResponseCode() throws IOException {
        return getConnection().getResponseCode();
    }

    @Override
    public Map<String, List<String>> getHeaderFields() throws IOException {
        return getConnection().getHeaderFields();
    }

    @Override
    public String getResponseMessage() throws IOException {
        return getConnection().getResponseMessage();
    }

}
