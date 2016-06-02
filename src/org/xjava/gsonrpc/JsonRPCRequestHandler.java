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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xjava.gsonrpc.annotation.RPCMethod;
import org.xjava.gsonrpc.annotation.RPCService;
import org.xjava.gsonrpc.exception.JsonRPCRuntimeException;
import org.xjava.gsonrpc.message.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @version 1.0
 * @author Adam Lowman
 */

public class JsonRPCRequestHandler {
  private final Gson gson;
  private final JsonRPCMessageFactory messageFactory;
  private final HashMap <String, RPCMethodData> rpcMethods;

  public JsonRPCRequestHandler(@NotNull Gson gson, @NotNull JsonRPCMessageFactory messageFactory) {
    this.gson = gson;
    this.messageFactory = messageFactory;
    rpcMethods = new HashMap<>();
  }

  public void addService(Class serviceClass) {
    addService(serviceClass, null);
  }

  public void addService(@NotNull Class serviceClass, @Nullable String namespace) {
    RPCService serviceAnnotation = (RPCService) serviceClass.getAnnotation(RPCService.class);

    if(namespace == null) {
      if (serviceAnnotation != null && !serviceAnnotation.namespace().isEmpty())
        namespace = serviceAnnotation.namespace();
      else
        namespace = serviceClass.getSimpleName();
    }

    Method[] methods = serviceClass.getMethods();
    for(Method method: methods) {
      if(method.isAnnotationPresent(RPCMethod.class))
        addMethod(method, namespace);
    }
  }

  public void addMethod(Method method) throws JsonRPCRuntimeException {
    addMethod(method, "", null);
  }

  public void addMethod(Method method, String namespace) throws JsonRPCRuntimeException {
    addMethod(method, namespace, null);
  }

  public void addMethod(@NotNull Method method, @NotNull String namespace, @Nullable String methodName) throws JsonRPCRuntimeException {
    if(!Modifier.isStatic(method.getModifiers()))
      throw new JsonRPCRuntimeException(method.getName() + " in " + method.getDeclaringClass().getCanonicalName() + " is non-static.");

    if("rpc".equalsIgnoreCase(namespace))
      throw new JsonRPCRuntimeException("The \"rpc\" namespace is reserved.");

    RPCMethod methodAnnotation = (RPCMethod) method.getAnnotation(RPCMethod.class);

    if(methodName == null || methodName.isEmpty()) {
      if(methodAnnotation != null && !methodAnnotation.name().isEmpty())
        methodName = methodAnnotation.name();
      else
        methodName = method.getName();
    }

    String rpcMethodName = (namespace.isEmpty() ? "" : namespace + ".") + methodName;

    if(rpcMethods.get(rpcMethodName) != null)
      throw new JsonRPCRuntimeException("A method named " + rpcMethodName + " is already defined.");

    ArrayList<String> paramNames = null;
    if(methodAnnotation != null && methodAnnotation.paramNames().length > 0) {
      if(methodAnnotation.paramNames().length != method.getParameterCount())
        throw new JsonRPCRuntimeException(rpcMethodName + " has a different number of parameters and paramNames.");

      paramNames = new ArrayList<>(Arrays.asList(methodAnnotation.paramNames()));
    }

    rpcMethods.put(rpcMethodName, new RPCMethodData(method, rpcMethodName, paramNames));
  }

  @NotNull
  public JsonRPCMessage handleRequest(@NotNull JsonRPCRequest request) {
    if(!GsonRPC.VERSION.equals(request.getVersion()))
      return messageFactory.newErrorResponse(request.getId(), JsonRPCError.UNSUPPORTED_VERSION);

    RPCMethodData rpcMethodData = rpcMethods.get(request.getMethod());
    if(rpcMethodData == null)
      return messageFactory.newErrorResponse(request.getId(), JsonRPCError.METHOD_NOT_FOUND);

    ArrayList <Object> params;
    try {
      params = parseParams(request.getParamsJson(), rpcMethodData);
    }
    catch(Exception e) {
      return messageFactory.newErrorResponse(request.getId(), JsonRPCError.INVALID_PARAMS);
    }

    if(params.size() != rpcMethodData.getMethod().getParameterCount())
      return messageFactory.newErrorResponse(request.getId(), JsonRPCError.INVALID_PARAMS);

    try {
      Object result = rpcMethodData.getMethod().invoke(null, params.toArray());
      return messageFactory.newResponse(request.getId(), gson.toJsonTree(result));
    }
    catch(Exception e) {
      return messageFactory.newErrorResponse(request.getId(), JsonRPCError.INTERNAL_ERROR);
    }
  }

  @NotNull
  private ArrayList <Object> parseParams(@Nullable JsonElement paramsJson, @NotNull RPCMethodData rpcMethodData) throws Exception {
    if (paramsJson == null || paramsJson.isJsonNull())
      return new ArrayList<>();
    else if (paramsJson.isJsonObject())
      return parseParams(paramsJson.getAsJsonObject(), rpcMethodData);
    else if (paramsJson.isJsonArray())
      return parseParams(paramsJson.getAsJsonArray(), rpcMethodData);
    else
      throw (new Exception());
  }

  @NotNull
  private ArrayList <Object> parseParams(@Nullable JsonObject paramsJson, @NotNull RPCMethodData rpcMethodData) throws Exception {
    if(paramsJson.entrySet().size() != rpcMethodData.getMethod().getParameterCount())
      throw new Exception();

    if(rpcMethodData.getParamNames() == null)
      throw new Exception();

    ArrayList<Object> params = new ArrayList<>();

    int i = 0;
    Class<?>[] paramTypes = rpcMethodData.getMethod().getParameterTypes();
    for(Class<?> paramType:paramTypes) {
      String paramName = rpcMethodData.getParamNames().get(i++);

      if(!paramsJson.has(paramName))
        throw new Exception();

      params.add(gson.fromJson(paramsJson.get(paramName), paramType));
    }

    return params;
  }

  @NotNull
  private ArrayList <Object> parseParams(@Nullable JsonArray paramsJson, @NotNull RPCMethodData rpcMethodData) throws Exception {
    if(paramsJson.size() != rpcMethodData.getMethod().getParameterCount())
      throw new Exception();

    ArrayList<Object> params = new ArrayList<>();

    int i = 0;
    Class<?>[] paramTypes = rpcMethodData.getMethod().getParameterTypes();
    for(Class<?> paramType:paramTypes)
      params.add(gson.fromJson(paramsJson.get(i++), paramType));

    return params;
  }

  private final class RPCMethodData {
    private final Method method;
    private final String methodName;
    private final ArrayList<String> paramNames;

    public RPCMethodData(@NotNull Method method, @NotNull String methodName, @Nullable ArrayList<String> paramNames) {
      this.method = method;
      this.methodName = methodName;
      this.paramNames = paramNames;
    }

    @NotNull
    public Method getMethod() {
      return method;
    }

    @NotNull
    public String getMethodName() {
      return methodName;
    }

    @Nullable
    public ArrayList<String> getParamNames() {
      return paramNames;
    }
  }
}
