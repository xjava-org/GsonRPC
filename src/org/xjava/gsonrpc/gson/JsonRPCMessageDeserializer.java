/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 xjava.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.xjava.gsonrpc.gson;

import com.google.gson.*;
import org.xjava.gsonrpc.JsonRPCError;
import org.xjava.gsonrpc.message.*;

import java.lang.reflect.Type;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCMessageDeserializer implements JsonDeserializer<JsonRPCMessage> {
  public JsonRPCMessage deserialize(JsonElement element, Type type, JsonDeserializationContext ctx) {
    String version = null;
    String id = null;

    try {
      JsonObject json = element.getAsJsonObject();

      version = json.get("jsonrpc").getAsString();
      id = json.get("id") == null || json.get("id").isJsonNull() ? null : json.get("id").getAsString();

      if (json.has("method")) {
        String method = json.get("method").getAsString();
        JsonElement paramsJson = json.get("params");
        return new JsonRPCRequest(version, id, method, paramsJson);
      }
      else if (json.has("result")) {
        JsonElement resultJson = json.get("result");
        return new JsonRPCResponse(version, id, resultJson);
      }
      else if (json.has("error")) {
        return new JsonRPCErrorResponse(version, id, JsonRPCError.INTERNAL_ERROR);
      }
    }
    catch (Exception e) {
      //Do nothing. The message is malformed, we don't care why.
    }

    return new JsonRPCMalformedMessage(version, id);
  }
}
