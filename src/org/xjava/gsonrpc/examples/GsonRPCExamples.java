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

package org.xjava.gsonrpc.examples;

import com.google.gson.GsonBuilder;
import org.xjava.gsonrpc.GsonRPC;
import org.xjava.gsonrpc.message.JsonRPCErrorResponse;
import org.xjava.gsonrpc.message.JsonRPCMessage;
import org.xjava.gsonrpc.message.JsonRPCResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Some examples showing ways to use GsonRPC.
 */
public class GsonRPCExamples {
  public static void main(String[] args) {

    GsonBuilder gsonBuilder = new GsonBuilder();

    /**
     * Use gsonBuilder.registerTypeAdapter(type, typeAdapter) to register any custom
     * serializers/deserializers you will need before passing gsonBuilder to GsonRPC.
     * If you don't need to do this, use the GsonRPC() constructor.
     */

    GsonRPC gsonRPC = new GsonRPC(gsonBuilder);

    setupRequestHandler(gsonRPC);

    String requestsJsonString = createRequestsUsingMessageFactory(gsonRPC);

    String responsesJsonString = processRequests(gsonRPC, requestsJsonString);

    getResults(gsonRPC, responsesJsonString);

    doRequestsUsingServiceProxy(gsonRPC);
  }

  //Set up the request handler
  private static void setupRequestHandler(GsonRPC gsonRPC) {
    System.out.println("Setting up the request handler...");

    //Add a service to the request handler
    ExampleServiceImplementation service = new ExampleServiceImplementation();
    gsonRPC.getRequestHandler().addService(service, ExampleServiceInterface.class);

    System.out.println("serviceExample service added.\n");
  }

  //Create requests using the message factory
  private static String createRequestsUsingMessageFactory(GsonRPC gsonRPC) {
    System.out.println("Creating requests using the message factory...");

    //Make a small batch of requests
    List<JsonRPCMessage> requests = new ArrayList<>();
    requests.add(gsonRPC.getMessageFactory().newRequest("1", "serviceExample.getMeaningOfLife"));
    requests.add(gsonRPC.getMessageFactory().newRequest("2", "serviceExample.makeLouder", "shout"));

    String requestsJsonString = gsonRPC.toJson(requests);

    System.out.println(
        "Requests:"
        + requestsJsonString
        + "\n"
    );

    return requestsJsonString;
  }

  //Process requests using the request handler
  private static String processRequests(GsonRPC gsonRPC, String requestsJsonString) {
    System.out.println("Processing requests...");

    //Process requests
    List<JsonRPCMessage> responses = gsonRPC.processRequests(requestsJsonString);

    String responsesJsonString = gsonRPC.toJson(responses);

    System.out.println(
        "Responses:"
            + responsesJsonString
            + "\n"
    );

    return responsesJsonString;
  }


  //Get results
  private static void getResults(GsonRPC gsonRPC, String responsesJsonString) {
    System.out.println("Getting results...");

    List<JsonRPCMessage> responses = gsonRPC.parseMessages(responsesJsonString);
    for(JsonRPCMessage message : responses) {
      if(message.isErrorResponse()) {
        JsonRPCErrorResponse errorResponse = message.getAsErrorResponse();

        System.out.println(
            "An error ("
            + errorResponse.getError().getCode()
            + " "
            + errorResponse.getError().getMessage()
            + ") occurred for request id="
            + errorResponse.getId()
        );
      }
      else if(message.isResponse()) {
        JsonRPCResponse response = message.getAsResponse();

        //Get the result as an object
        Object result = gsonRPC.getResult(response, Object.class);

        System.out.println(
            "Request id="
            + response.getId()
            + " returned result "
            + result.toString()
        );
      }
    }

    System.out.println("");
  }

  //Do requests using a service proxy
  private static void doRequestsUsingServiceProxy(GsonRPC gsonRPC) {
    System.out.println("Doing requests using a service proxy...");

    //Create a service proxy
    ExampleServiceInterface serviceProxy = gsonRPC.getProxyFactory().newServiceProxy(ExampleServiceInterface.class, requestJson -> {

      //Normally you would transmit the request JSON and return the result JSON upon reception
      return gsonRPC.toJson(gsonRPC.processRequests(requestJson));
    });

    try {
      Integer result = serviceProxy.getMeaningOfLife();
      System.out.println("Result: " + result);
    }
    catch(Exception e) {
      System.out.println(e.getCause().getMessage());
    }

    try {
      String result = serviceProxy.makeLouder("shout");
      System.out.println("Result: " + result);
    }
    catch(Exception e) {
      System.out.println(e.getCause().getMessage());
    }
  }
}
