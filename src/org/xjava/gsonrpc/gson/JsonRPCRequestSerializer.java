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
import org.xjava.gsonrpc.message.JsonRPCRequest;

import java.lang.reflect.Type;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCRequestSerializer implements JsonSerializer<JsonRPCRequest> {

  @Override
  public JsonElement serialize(JsonRPCRequest request, Type type, JsonSerializationContext ctx) {
    JsonObject json = new JsonObject();

    json.addProperty("jsonrpc", request.getVersion());
    json.addProperty("id", request.getId());
    json.addProperty("method", request.getMethod());

    if(request.getParamsJson() != null && !request.getParamsJson().isJsonNull())
      json.add("params", request.getParamsJson());

    return json;
  }
}