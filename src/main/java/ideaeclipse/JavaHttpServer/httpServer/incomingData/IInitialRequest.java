package ideaeclipse.JavaHttpServer.httpServer.incomingData;

import ideaeclipse.JavaHttpServer.httpServer.responses.objects.RequestMethod;

import java.util.Map;

/**
 * Used to parse the header when a request is send
 *
 * @author Myles T
 */
public interface IInitialRequest {
    /**
     * The Method is the request method
     * @see RequestMethod
     * @return request method
     */
    RequestMethod getMethod();

    /**
     * Remote directory the user is trying to access
     * i.e http://localhost/api/login
     * the directory would be /api/login
     * for params see {@link #getParams()}
     * @return directory user is string to access
     */
    String getDirectory();

    /**
     * Directory params the user has passed to the directory
     * i.e http://localhost/api/login?token=asd
     * the map would contain 1 param {token=asd}
     * @return map of params
     */
    Map<String, String> getParams();

    /**
     * The connection address the user connected from
     * This is the ipaddress the user connected with. If they connected through a domain
     * it will the domain name not the ip address.
     * If you're connecting locally this could be localhost not 127.0.0.1
     * @return users ip-address
     */
    String getConnectionAddress();

    /**
     * This is a map of all the remaining header information.
     * This allows you to write custom header methods for internal use
     * you can parse this inside the your end points
     * @return map of the remaining header information
     */
    Map<String, String> getAdditionalHeaderInfo();

    /**
     * This is byte data written to the socket. For example if you sent a json string the parse will read the json
     * as a string and you can parse it into a json object. This is parsed using the param Content-Length. If this value
     * is not present the data will not be read
     * @return data written to the socket
     */
    String getPassedData();
}
