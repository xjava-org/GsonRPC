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

import com.google.gson.*;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xjava.gsonrpc.gson.*;
import org.xjava.gsonrpc.message.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A Gson based Java library for creating and/or processing JSON-RPC 2.0 requests following the specification at
 * http://jsonrpc.org/specification
 *
 * @version 1.0
 * @author Adam Lowman
 *
 */
public class GsonRPC {
  public static final String VERSION = "2.0";

  private final Gson gson;
  private final JsonParser parser = new JsonParser();
  private final JsonRPCMessageFactory messageFactory;
  private final JsonRPCRequestHandler requestHandler;

  /**
   * Constructs an instance of GsonRPC without a supplied GsonBuilder.
   */
  public GsonRPC() {
    this(new GsonBuilder());
  }

  /**
   * Constructs an instance of GsonRPC using a supplied GsonBuilder. This allows you to register any custom Gson
   * serializers/deserializers you will need (all other GsonBuilder options may be overwritten).
   *
   * <p>To initialize GsonRPC with a GsonBuilder use the following code:</p>
   *
   * <pre>{@code
   * GsonBuilder gsonBuilder = new GsonBuilder();
   * gsonBuilder.registerTypeAdapter(CustomType.class, new CustomTypeSerializer()); //OPTIONAL
   * gsonBuilder.registerTypeAdapter(CustomType.class, new CustomTypeDeserializer()); //OPTIONAL
   * GsonRPC gsonRPC = new GsonRPC(gsonBuilder);
   * }</pre>
   *
   * <p>NOTE: Be sure to use gsonBuilder.registerTypeAdapter(type, typeAdapter) to register any custom
   * serializers/deserializers you will need before passing gsonBuilder to GsonRPC.</p>
   *
   * @param gsonBuilder The GsonBuilder
   */
  public GsonRPC(GsonBuilder gsonBuilder) {
    gson = gsonBuilder
        .serializeNulls()
        .registerTypeAdapter(JsonRPCMessage.class, new JsonRPCMessageDeserializer())
        .registerTypeAdapter(JsonRPCRequest.class, new JsonRPCRequestSerializer())
        .registerTypeAdapter(JsonRPCResponse.class, new JsonRPCResponseSerializer())
        .registerTypeAdapter(JsonRPCErrorResponse.class, new JsonRPCErrorResponseSerializer())
        .create();

    messageFactory = new JsonRPCMessageFactory(gson);
    requestHandler = new JsonRPCRequestHandler(gson, messageFactory);
  }

  /**
   * Converts a JsonRPCMessage to a JSON encoded String. An empty String will be returned if the message is null.
   *
   * @param message The JsonRPCMessage
   * @return The JSON encoded String
   */
  @NotNull
  public String toJson(@Nullable JsonRPCMessage message) {
    if(message == null)
      return "";

    return gson.toJson(message);
  }

  /**
   * Converts a JsonRPCMessage to a JsonElement. A null will be returned if the message is null.
   *
   * @param message The JsonRPCMessage
   * @return The JsonElement
   */
  @Nullable
  public JsonElement toJsonTree(@Nullable JsonRPCMessage message) {
    if(message == null)
      return null;

    return gson.toJsonTree(message);
  }

  /**
   * Converts a List of JsonRPCMessage objects to a JSON encoded String. An empty String will be returned if the List is empty is null.
   *
   * @param messages The List of JsonRPCMessage objects
   * @return The JSON encoded String
   */
  @NotNull
  public String toJson(@Nullable List<JsonRPCMessage> messages) {
    JsonElement messagesJson = toJsonTree(messages);

    if(messagesJson == null)
      return "";

    return messagesJson.toString();
  }

  /**
   * Converts a List of JsonRPCMessage objects to a JsonElement. A null will be returned if the List is empty is null.
   *
   * @param messages The List of JsonRPCMessage objects
   * @return The JsonElement
   */
  @Nullable
  public JsonElement toJsonTree(@Nullable List<JsonRPCMessage> messages) {
    if(messages.size() > 1)
      return gson.toJsonTree(messages);
    else if(messages.size() == 1)
      return gson.toJsonTree(messages.get(0));
    else
      return null;
  }

  /**
   * Gets the result of a JsonRPCResponse as an Object.
   *
   * @param response The JsonRPCResponse
   * @param resultClass The Class of the result
   * @return The result
   */
  @Nullable
  public <T> T getResult(JsonRPCResponse response, Class<T> resultClass) {
    return gson.fromJson(response.getResultJson(), resultClass);
  }

  /**
   * Gets the data of a JsonRPCErrorResponse as an Object.
   *
   * @param errorResponse The JsonRPCErrorResponse
   * @param dataClass The Class of the data
   * @return The data
   */
  @Nullable
  public <T> T getData(JsonRPCErrorResponse errorResponse, Class<T> dataClass) {
    return gson.fromJson(errorResponse.getDataJson(), dataClass);
  }

  /**
   * Gets the JsonRPCMessageFactory.
   *
   * @return The JsonRPCMessageFactory
   */
  @NotNull
  public JsonRPCMessageFactory getMessageFactory() {
    return messageFactory;
  }

  /**
   * Gets the JsonRPCRequestHandler.
   *
   * @return The JsonRPCRequestHandler
   */
  @NotNull
  public JsonRPCRequestHandler getRequestHandler() {
    return requestHandler;
  }

  /**
   * Parses a JSON encoded String of GsonRPC messages.
   *
   * @param messagesString The JSON encoded String of GsonRPC messages
   * @return A List of JsonRPCMessage objects
   * @throws JsonSyntaxException A JsonSyntaxException may be thrown if Gson encounters an issue parsing messagesString
   */
  @NotNull
  public List<JsonRPCMessage> parseMessages(@Nullable String messagesString) throws JsonSyntaxException {
    return parseMessages(parser.parse(messagesString));
  }

  /**
   * Parses a JsonElement containing GsonRPC messages.
   *
   * @param messagesJson The JsonElement containing GsonRPC messages
   * @return A List of JsonRPCMessage objects
   */
  @NotNull
  public List<JsonRPCMessage> parseMessages(@Nullable JsonElement messagesJson) {
    List<JsonRPCMessage> messages = new ArrayList<>();

    if(messagesJson == null || messagesJson.isJsonNull())
      messages.add(new JsonRPCMalformedMessage());
    else {
      try {
        if (messagesJson.isJsonArray())
          messagesJson.getAsJsonArray().forEach(messageJson -> messages.add(gson.fromJson(messageJson, JsonRPCMessage.class)));
        else
          messages.add(gson.fromJson(messagesJson, JsonRPCMessage.class));
      }
      catch (Exception e) {
        messages.add(new JsonRPCMalformedMessage());
      }
    }

    return messages;
  }

  /**
   * Processes the requests contained in a JSON encoded String of GsonRPC messages.
   *
   * @param messagesString The JSON encoded String of GsonRPC messages
   * @return A List of JsonRPCMessage responses
   */
  @NotNull
  public List<JsonRPCMessage> processRequests(@Nullable String messagesString) {
    List<JsonRPCMessage> messages;

    try {
      messages = parseMessages(messagesString);
    }
    catch(Exception e) {
      List<JsonRPCMessage> responses = new ArrayList<>();
      responses.add(messageFactory.newErrorResponse(null, JsonRPCError.PARSE_ERROR));
      return responses;
    }

    return processRequests(messages);
  }

  /**
   * Processes the requests contained in a JsonElement.
   *
   * @param messagesJson The JsonElement
   * @return A List of JsonRPCMessage responses
   */
  @NotNull
  public List<JsonRPCMessage> processRequests(@Nullable JsonElement messagesJson) {
    return processRequests(parseMessages(messagesJson));
  }

  /**
   * Processes the requests contained in a List of JsonRPCMessage objects.
   *
   * @param messages The List of JsonRPCMessage objects
   * @return A List of JsonRPCMessage responses
   */
  @NotNull
  public List<JsonRPCMessage> processRequests(@Nullable List<JsonRPCMessage> messages) {
    List<JsonRPCMessage> responses = new ArrayList<>();

    if(messages != null) {
      messages.forEach(message -> {
        if (message.isMalformedMessage())
          responses.add(messageFactory.newErrorResponse(message.getId(), JsonRPCError.INVALID_REQUEST));
        else if (message.isRequest()) {
          JsonRPCRequest request = message.getAsRequest();
          JsonRPCMessage response = requestHandler.handleRequest(request);

          if (!request.isNotification())
            responses.add(response);
        }
      });
    }

    return responses;
  }
}
