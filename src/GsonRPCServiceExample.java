import org.xjava.gsonrpc.annotation.RPCMethod;
import org.xjava.gsonrpc.annotation.RPCService;

/**
 * An example GsonRPC service class using annotations.
 */

@RPCService(namespace = "serviceExample")
public class GsonRPCServiceExample {

  @RPCMethod
  public static Integer getMeaningOfLife() {
    return 42;
  }

  @RPCMethod( paramNames = {"message"} )
  public static String makeLouder(String message) {
    return message.toUpperCase();
  }
}
