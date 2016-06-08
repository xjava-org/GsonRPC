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
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xjava.gsonrpc.proxy.JsonRPCResponseGetter;
import org.xjava.gsonrpc.proxy.JsonRPCServiceProxy;

import java.lang.reflect.Proxy;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCProxyFactory {

  private GsonRPC gsonRPC;

  public JsonRPCProxyFactory(@NotNull GsonRPC gsonRPC) {
    this.gsonRPC = gsonRPC;
  }

  public <T> T newServiceProxy(@NotNull Class<T> serviceInterface, @NotNull JsonRPCResponseGetter responseGetter) {
    return newServiceProxy(serviceInterface, null, responseGetter);
  }

  public <T> T newServiceProxy(@NotNull Class<T> serviceInterface, @Nullable String namespace, @NotNull JsonRPCResponseGetter responseGetter) {
    JsonRPCServiceProxy serviceProxy = new JsonRPCServiceProxy(gsonRPC, serviceInterface, namespace, responseGetter);
    return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[] { serviceInterface }, serviceProxy);
  }
}
