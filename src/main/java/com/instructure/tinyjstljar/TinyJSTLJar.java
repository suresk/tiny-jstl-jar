package com.instructure.tinyjstljar;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;

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
  public static void main(String[] args) throws Exception {
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

    //Ensure the jsp engine is initialized correctly
    JettyJasperInitializer sci = new JettyJasperInitializer();
    ServletContainerInitializersStarter sciStarter = new ServletContainerInitializersStarter(dynamicHandler);
    ContainerInitializer initializer = new ContainerInitializer(sci, null);
    List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
    initializers.add(initializer);

    dynamicHandler.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
    dynamicHandler.addBean(sciStarter, true);

    ServletHolder holderJsp = new ServletHolder("jsp",JspServlet.class);
    holderJsp.setInitOrder(0);
    holderJsp.setInitParameter("logVerbosityLevel","DEBUG");
    holderJsp.setInitParameter("fork","false");
    holderJsp.setInitParameter("xpoweredBy","false");
    holderJsp.setInitParameter("compilerTargetVM","1.7");
    holderJsp.setInitParameter("compilerSourceVM","1.7");
    holderJsp.setInitParameter("keepgenerated","true");
    dynamicHandler.addServlet(holderJsp,"*.jsp");

    try {
      webserver.start();
      webserver.join();
    } catch (Exception e) {
      System.out.println("whoops");
    }
  }
}
