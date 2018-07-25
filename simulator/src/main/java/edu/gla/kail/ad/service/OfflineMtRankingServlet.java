package edu.gla.kail.ad.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.gson.JsonObject;

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
            case "validateUserAndStartExperiment":
                response.getWriter().write(verifyUser(userId).toString());
                break;
            case "loadTasks":
                Integer maxTasksAssigned = Integer.valueOf(request.getParameter("maxTasksAssigned"));
                response.getWriter().write(loadTasks(userId, maxTasksAssigned));
                break;
            case "rateTask":
                Integer ratingScore = Integer.valueOf(request.getParameter("ratingScore"));
                String taskId = request.getParameter("taskId");
                response.getWriter().write(rateTask(userId, ratingScore, taskId));
                break;
            default:
                response.getWriter().write("false");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    private String loadTasks(String userId, Integer maxTasksAssigned) {
        JsonObject json = new JsonObject();
        //     check if the user has any tasks assigned,
        //         not maxNum..:
        //             assign more tasks
        //     download assigned tasks
        return json.toString();
    }
    private String rateTask(String userId, Integer ratingScore, String taskId) {
        JsonObject json = new JsonObject();
        json.addProperty("sth", "value");
        // send a rating to servlet:
        // Update user dabatase - remove task reference from user list
        // update rating database
        // update rating reference list of a task in Firestore
        return json.toString();
    }



    private Boolean verifyUser(String userId) {
        if (userId == null || userId.equals("")) {
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
