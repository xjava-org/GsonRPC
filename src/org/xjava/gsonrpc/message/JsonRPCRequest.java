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

package org.xjava.gsonrpc.message;

import com.google.gson.JsonElement;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCRequest extends JsonRPCMessage {
  private final String method;
  private final JsonElement paramsJson;

  public JsonRPCRequest(@NotNull String version, @Nullable String id, @NotNull String method, @Nullable JsonElement paramsJson) {
    super(version, id);
    this.method = method;
    this.paramsJson = paramsJson;
  }

  public String getMethod() { return method; }

  public JsonElement getParamsJson() {
    return paramsJson;
  }

  public boolean isNotification() { return id == null || id.isEmpty(); }
}
