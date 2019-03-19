# JavaHttpServer
* The purpose of this http server is to allow you to be able to host a website with as little effort as instantiating an object and writing a couple lines of code to serv up a page
* The reason I created this utility is because I found myself in a position where using a tomcat project to host a page was not a viable thing.
* I created this to use this a Oauth2.0 redirct uri server originally but added support to program which pages got sent as reponses based on which directory/method was requested
* This utility uses two of my previously created utilities called [reflectionListener](https://github.com/ideaeclipse/ReflectionListener), [JsonUtilities](https://github.com/ideaeclipse/JsonUtilities) and [CustomProperties](https://github.com/ideaeclipse/CustomProperties)

## Starting the server
* In order to start the server you must first pass the port number you wish for the web server to start on and a [Listener object](https://github.com/ideaeclipse/ReflectionListener)
```java
new Server(8080, new ConnectionListener());
```
* This would start the server on port 8080 and would collect all methods from the class ConnectionListener

## Custom Properties
* The custom properties util will prompt you to enter two pieces of data
* one is where your html files are located, in my test example they're in a directory called "pages"
* the second parameter is which directory in the webserver will be serving up a 404 page, mine is "/404"

## Connection Event
* The connection event is the only parameter your listener methods should have
* The connection event passes you the parameters from the url and gives you a writer object which allows you to send a response to the user based on their request type

## Listener
* When you create a listener it needs to implement the Listener interface from the reflectionListener package
* Each method you have must have the single parameter of Connection Event
* Each method must have the @EventHandler annotation along with @PageData
* PageData has to parameters. One is the method type while the other is the directory.
* Default method is get and default directory is "/"
```java
public class ConnectionListener implements Listener {
    @EventHandler
    @PageData
    public Boolean connect(ConnectionEvent event) {
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "Page.html"));
    }
}
```
* For example this connection listener will only server up pages when the "/" directory is requested with the method type of get
* It will respond with a new page titled "Page.html" with a response code of 200 "OK"
* More info on responses is below

## Responses
* Currently there are two different responses: Json Responses and Html Responses
* Json responses require a response code and a [Json Object](https://github.com/ideaeclipse/JsonUtilities)
```java
@EventHandler
@PageData(directory = "/jsonTest")
public Boolean jsonTest(ConnectionEvent event) {
    Json json = new Json();
    json.put("SomeKey", "SomeValue");
    return event.getWriter().sendPage(new JsonResponse(new Header(ResponseCodes.Code_200), json));
}
```
* This would return, this functionality could be used to create a custom rest service
```java
{"SomeKey":"SomeValue"}
```
* Html responses require a response code and a File name that you would like to show up
```java
@EventHandler   
@PageData(directory = "/bootstrapTest")
public Boolean bootstrapTest(ConnectionEvent event) {
    return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "BootStrapTest.html"));
}
```

## Custom Ejs like use cases
* For example if you wanted to have dynamic pages that say the title of the page was dynamic to which directory you were in you could set something like the example below
```html
<TITLE><%= title %></TITLE>
```
* When passing your Page object to the writer you need to add a map that contains the variable names as the key and the values you wish to replce them with
```java
public class ConnectionListener implements Listener {
    @EventHandler
    @PageData
    public Boolean connect(ConnectionEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Home page");
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "Page.html", map)); 
    }
}
```
* This example will set the title as "Home page"
* If the util is unable to find a valid variable to replace the notation with it will replace it with an upside down question mark

## Methods outside of get
* If you would like to have a method that is only accessible using the post method you could do
```java
public class ConnectionListener implements Listener {
    @EventHandler
    @PageData(method = PageData.Method.POST, directory = "/postTest")
    public Boolean postTest(ConnectionEvent event) {
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "PostTest.html")); 
    }
}
```
* This would will only be called when a user requests the directory: "/postTest" with the post method
* You can customize the response with whatever you'd like to do. Whether that be modifying database information or changing objects

