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

package org.xjava.gsonrpc.proxy;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xjava.gsonrpc.GsonRPC;
import org.xjava.gsonrpc.JsonRPCError;
import org.xjava.gsonrpc.JsonRPCMessageFactory;
import org.xjava.gsonrpc.annotation.RPCMethod;
import org.xjava.gsonrpc.annotation.RPCService;
import org.xjava.gsonrpc.exception.JsonRPCErrorException;
import org.xjava.gsonrpc.message.JsonRPCMessage;
import org.xjava.gsonrpc.message.JsonRPCResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCServiceProxy implements InvocationHandler {

  private GsonRPC gsonRPC;
  private String namespace;
  private JsonRPCResponseGetter responseGetter;

  public JsonRPCServiceProxy(@NotNull GsonRPC gsonRPC, @NotNull Class serviceInterface, @Nullable String namespace, @NotNull JsonRPCResponseGetter responseGetter) {
    this.gsonRPC = gsonRPC;
    this.responseGetter = responseGetter;

    RPCService serviceAnnotation = (RPCService) serviceInterface.getAnnotation(RPCService.class);
    if(namespace == null) {
      if (serviceAnnotation != null && !serviceAnnotation.namespace().isEmpty())
        this.namespace = serviceAnnotation.namespace();
      else
        this.namespace = serviceInterface.getSimpleName();
    }
    else
      this.namespace = namespace;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    RPCMethod methodAnnotation = (RPCMethod) method.getAnnotation(RPCMethod.class);

    String id = method.getReturnType().equals(Void.TYPE) ? null : UUID.randomUUID().toString();
    String methodName = methodAnnotation != null && !methodAnnotation.name().isEmpty() ? methodAnnotation.name() :  method.getName();
    List<Object> params = args == null ? null : Arrays.asList(args);

    String requestJson = gsonRPC.toJson(gsonRPC.getMessageFactory().newRequestWithParams(id, namespace+"."+methodName, params));

    String responseJson = responseGetter.getResponse(requestJson);

    List<JsonRPCMessage> responses = gsonRPC.parseMessages(responseJson);

    if(responses.size() > 1)
      throw new JsonRPCErrorException(JsonRPCError.INTERNAL_ERROR);

    if(method.getReturnType().equals(Void.TYPE)) {
      if(responses.size() == 1) {
        JsonRPCMessage message = responses.get(0);
        if (message.isErrorResponse())
          throw new JsonRPCErrorException(message.getAsErrorResponse());
      }

      return null;
    }

    if(responses.size() == 0)
      throw new JsonRPCErrorException(JsonRPCError.INTERNAL_ERROR);

    JsonRPCMessage message = responses.get(0);

    if(message.isResponse()) {
      JsonRPCResponse response = message.getAsResponse();

      if(!response.getId().equals(id))
        throw new JsonRPCErrorException(JsonRPCError.INTERNAL_ERROR);

      return gsonRPC.getResult(response, method.getReturnType());
    }
    else if(message.isErrorResponse())
      throw new JsonRPCErrorException(message.getAsErrorResponse());

    throw new JsonRPCErrorException(JsonRPCError.INTERNAL_ERROR);
  }
}
