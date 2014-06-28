package com.instructure.tinyjstljar;

import java.util.UUID;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConsumerServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    UUID key = UUID.randomUUID();
    request.setAttribute("key", key);
    request.getRequestDispatcher("/consumer.jsp").forward(request, response);
  }
}
