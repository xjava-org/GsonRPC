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

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public abstract class JsonRPCMessage {
  protected final String version;
  protected final String id;

  public JsonRPCMessage(@NotNull String version, @Nullable String id) {
    this.version = version;
    this.id = id;
  }

  public final String getVersion() {
    return version;
  }

  public final String getId() {
    return id;
  }

  public final boolean isMalformedMessage() { return this instanceof JsonRPCMalformedMessage; }

  public final boolean isRequest() { return this instanceof JsonRPCRequest; }

  public final boolean isResponse() { return this instanceof JsonRPCResponse; }

  public final boolean isErrorResponse() { return this instanceof JsonRPCErrorResponse; }

  public final JsonRPCMalformedMessage getAsMalformedMessage() {
    if(isMalformedMessage())
      return (JsonRPCMalformedMessage) this;
    else
      throw new IllegalStateException("This is not a malformed message.");
  }

  public final JsonRPCRequest getAsRequest() {
    if(isRequest())
      return (JsonRPCRequest) this;
    else
      throw new IllegalStateException("This is not a request.");
  }

  public final JsonRPCResponse getAsResponse() {
    if(isResponse())
      return (JsonRPCResponse) this;
    else
      throw new IllegalStateException("This is not a response.");
  }

  public final JsonRPCErrorResponse getAsErrorResponse() {
    if(isErrorResponse())
      return (JsonRPCErrorResponse) this;
    else
      throw new IllegalStateException("This is not an error response.");
  }
}
