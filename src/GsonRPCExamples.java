import com.google.gson.GsonBuilder;
import org.xjava.gsonrpc.GsonRPC;
import org.xjava.gsonrpc.message.JsonRPCMessage;

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

    //Add a service to the request handler
    gsonRPC.getRequestHandler().addService(GsonRPCServiceExample.class);

    //Make a small batch of requests
    List<JsonRPCMessage> requests = new ArrayList<>();
    requests.add(gsonRPC.getMessageFactory().newRequest("1", "serviceExample.getMeaningOfLife"));
    requests.add(gsonRPC.getMessageFactory().newRequest("2", "serviceExample.makeLouder", "shout"));

    String requestsJsonString = gsonRPC.toJson(requests);

    System.out.println("Requests:");
    System.out.println(requestsJsonString);

    //Process requests
    List<JsonRPCMessage> responses = gsonRPC.processRequests(requestsJsonString);

    String responsesJsonString = gsonRPC.toJson(responses);

    System.out.println("Responses:");
    System.out.println(responsesJsonString);
  }
}
