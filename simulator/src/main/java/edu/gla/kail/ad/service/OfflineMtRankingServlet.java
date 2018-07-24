package edu.gla.kail.ad.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@WebServlet("/offline-mt-ranking-servlet")
public class OfflineMtRankingServlet extends HttpServlet {
    private Firestore _database = LogManagerSingleton.returnDatabase();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
            IOException {
        String userId = request.getParameter("userId");
        switch (request.getHeader("operation")) {
            case "validateUser":
                response.getWriter().write(verifyUser(userId).toString());
                break;
            case "rateTask":
                break;
            default:
                System.out.println("well");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    private Boolean verifyUser(String userId) {
        if (userId == null || userId == "") {
            return false;
        }
        DocumentReference docRef = _database.collection("clientWebSimulator").document
                ("agent-dialogue-experiments").collection("users").document(userId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            if (future.get().exists()) {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;

    }
}
