package com.instructure.tinyjstljar;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class TinyJSTLJar {
  public static void main(String[] args) {
    Server webserver = new Server(8080);
    webserver.setSessionIdManager(new HashSessionIdManager());
    
    WebAppContext dynamicHandler = new WebAppContext();
    String webDir = TinyJSTLJar.class.getClassLoader().getResource("web").toExternalForm();
    dynamicHandler.setResourceBase(webDir);
    dynamicHandler.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
    ClassLoader jspClassLoader = new URLClassLoader(new URL[0], TinyJSTLJar.class.getClassLoader());
    dynamicHandler.setClassLoader(jspClassLoader);
    
    dynamicHandler.addServlet(new ServletHolder(new ConsumerServlet()),"/consumer");
    
    ResourceHandler staticHandler = new ResourceHandler();
    String staticDir = TinyJSTLJar.class.getClassLoader().getResource("static").toExternalForm();
    staticHandler.setResourceBase(staticDir);
    
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { staticHandler, dynamicHandler, new DefaultHandler() });
    webserver.setHandler(handlers);
    try {
      webserver.start();
      webserver.join();
    } catch (Exception e) {
      System.out.println("whoops");
    }
  }
}
