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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.xjava.gsonrpc.message.JsonRPCErrorResponse;

import java.lang.reflect.Type;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCErrorResponseSerializer implements JsonSerializer<JsonRPCErrorResponse> {

  @Override
  public JsonElement serialize(JsonRPCErrorResponse errorResponse, Type type, JsonSerializationContext ctx) {
    JsonObject json = new JsonObject();

    json.addProperty("jsonrpc", errorResponse.getVersion());
    json.addProperty("id", errorResponse.getId());

    JsonObject error = ctx.serialize(errorResponse.getError()).getAsJsonObject();

    if(errorResponse.getDataJson() != null && !errorResponse.getDataJson().isJsonNull())
      error.add("data", errorResponse.getDataJson());

    json.add("error", error);

    return json;
  }
}