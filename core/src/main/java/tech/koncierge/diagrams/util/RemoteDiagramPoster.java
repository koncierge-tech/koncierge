/*
Copyright 2023 The Koncierge Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package tech.koncierge.diagrams.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RemoteDiagramPoster {

    private static Logger logger = LoggerFactory.getLogger(RemoteDiagramPoster.class);

    public static String postDiagramRequest(String json) {
        URL url = null;
        try {
            url = new URL("https://k8s.envisionit.app/api/generate-diagram");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            con.getOutputStream().write(json.getBytes("utf-8"));
            String response = con.getResponseMessage();
            int responseCode = con.getResponseCode();
            logger.info("Response code: {}", responseCode);
            if (responseCode != 200) {
                InputStream errorStream = con.getErrorStream();
                String text = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                throw new RuntimeException("Error generating diagram: " + text);
            }
            InputStream inputStream = con.getInputStream();
            String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            logger.debug("Response body: {}", text);
            con.disconnect();
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
