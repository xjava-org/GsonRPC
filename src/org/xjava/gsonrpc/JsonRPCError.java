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

import com.sun.istack.internal.NotNull;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCError {

  public static final JsonRPCError PARSE_ERROR = new JsonRPCError(-32700, "Parse error");
  public static final JsonRPCError UNSUPPORTED_VERSION = new JsonRPCError(-32600, "Unsupported JSON-RPC version");
  public static final JsonRPCError INVALID_REQUEST = new JsonRPCError(-32600, "Invalid request");
  public static final JsonRPCError METHOD_NOT_FOUND = new JsonRPCError(-32601, "Method not found");
  public static final JsonRPCError INVALID_PARAMS = new JsonRPCError(-32602, "Invalid params");
  public static final JsonRPCError INTERNAL_ERROR = new JsonRPCError(-32603, "Internal error");

  private final int code;
  private final String message;

  public JsonRPCError(int code, @NotNull String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
