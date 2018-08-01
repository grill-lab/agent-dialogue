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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
                Integer maxTasksAssigned = Integer.valueOf(request.getParameter
                        ("maxTasksAssigned"));
                OfflineExperimentTaskLoader offlineExperimentTaskLoader = new OfflineExperimentTaskLoader();
                response.getWriter().write(offlineExperimentTaskLoader.loadTasks(_database, userId, maxTasksAssigned));
                break;
            case "rateTask":
                Integer ratingScore = Integer.valueOf(request.getParameter("ratingScore"));
                String taskId = request.getParameter("taskId");
                Long startTime_seconds = Long.valueOf(request.getParameter
                        ("startTime_seconds"));
                Long endTime_seconds = Long.valueOf(request.getParameter("endTime_seconds"));
                OfflineExperimentTaskRater offlineExperimentTaskRater = new OfflineExperimentTaskRater();
                offlineExperimentTaskRater.rateTask(_database, userId, ratingScore, taskId, startTime_seconds, endTime_seconds);
                break;
            default:
                response.getWriter().write("false");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    private Boolean verifyUser(String userId) {
        if (userId == null || userId.equals("")) {
            return false;
        }
        DocumentReference docRef = _database.collection("clientWebSimulator")
                .document("agent-dialogue-experiments").collection("users")
                .document(userId);
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
