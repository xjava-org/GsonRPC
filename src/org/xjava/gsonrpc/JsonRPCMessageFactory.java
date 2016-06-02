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

package org.xjava.gsonrpc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xjava.gsonrpc.message.JsonRPCErrorResponse;
import org.xjava.gsonrpc.message.JsonRPCRequest;
import org.xjava.gsonrpc.message.JsonRPCResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCMessageFactory {

  private final Gson gson;

  public JsonRPCMessageFactory(@NotNull Gson gson) {
    this.gson = gson;
  }

  @NotNull
  public JsonRPCRequest newRequest(@Nullable String id, @NotNull String method, Object... params) {
    return newRequestWithParams(id, method, Arrays.asList(params));
  }

  @NotNull
  public JsonRPCRequest newRequestWithParams(@Nullable String id, @NotNull String method, @Nullable HashMap<String,Object> params) {
    if(params == null || params.isEmpty())
      return new JsonRPCRequest(GsonRPC.VERSION, id, method, null);
    return new JsonRPCRequest(GsonRPC.VERSION, id, method, gson.toJsonTree(params));
  }

  @NotNull
  public JsonRPCRequest newRequestWithParams(@Nullable String id, @NotNull String method, @Nullable List<Object> params) {
    if(params == null || params.isEmpty())
      return new JsonRPCRequest(GsonRPC.VERSION, id, method, null);
    return new JsonRPCRequest(GsonRPC.VERSION, id, method, gson.toJsonTree(params));
  }


  @NotNull
  public JsonRPCResponse newResponse(@Nullable String id, @Nullable Object response) {
    JsonElement responseJson = gson.toJsonTree(response);
    return new JsonRPCResponse(GsonRPC.VERSION, id, responseJson);
  }

  @NotNull
  public JsonRPCErrorResponse newErrorResponse(@Nullable String id, @NotNull JsonRPCError error) {
    return newErrorResponse(id, error, null);
  }

  @NotNull
  public JsonRPCErrorResponse newErrorResponse(@Nullable String id, @NotNull JsonRPCError error, @Nullable Object data) {
    if(data == null)
      return new JsonRPCErrorResponse(GsonRPC.VERSION, id, error);
    else {
      JsonElement dataJson = gson.toJsonTree(data);
      return new JsonRPCErrorResponse(GsonRPC.VERSION, id, error, dataJson);
    }
  }
}
