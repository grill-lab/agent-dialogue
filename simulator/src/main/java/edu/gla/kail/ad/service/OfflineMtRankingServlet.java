package edu.gla.kail.ad.service;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/offline-mt-ranking-servlet")
public class OfflineMtRankingServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) {

        System.out.println(request.getParameterMap().toString());

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }


}
