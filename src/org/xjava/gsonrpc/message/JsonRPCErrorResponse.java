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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xjava.gsonrpc.JsonRPCError;

/**
 * @version 1.0
 * @author Adam Lowman
 */
public class JsonRPCErrorResponse extends JsonRPCMessage {
  private final JsonRPCError error;
  private final JsonElement dataJson;

  public JsonRPCErrorResponse(@NotNull String version, @Nullable String id, @NotNull JsonRPCError error) {
    this(version, id, error, null);
  }

  public JsonRPCErrorResponse(@NotNull String version, @Nullable String id, @NotNull JsonRPCError error, @Nullable JsonElement dataJson) {
    super(version, id);
    this.error = error;
    this.dataJson = dataJson;
  }

  public JsonRPCError getError() {
    return error;
  }

  public JsonElement getDataJson() {
    return dataJson;
  }

  /**
   * Gets the data as an Object.
   *
   * @param gson The instance of gson used for parsing JSON
   * @param dataClass The Class of the data
   * @return The data
   */
  @Nullable
  public <T> T getData(Gson gson, Class<T> dataClass) {
    return gson.fromJson(getDataJson(), dataClass);
  }
}
