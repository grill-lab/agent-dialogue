package edu.gla.kail.ad.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.gson.JsonObject;
import com.google.protobuf.Timestamp;
import com.sun.tools.internal.xjc.Language;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
        DocumentReference userDocRef = _database.collection("clientWebSimulator").document
                ("agent-dialogue-experiments").collection("users").document(userId);
        ApiFuture<DocumentSnapshot> userFuture = userDocRef.get();
        try {
            DocumentSnapshot user = userFuture.get();
            if (user.exists()) {
                Map<String, Object> userData = user.getData();
                ArrayList<String> listOfOpenTaskIds = (ArrayList<String>) userData.get
                        ("openTaskIds");
                int numOpenReferences = 0;
                if (listOfOpenTaskIds != null) {
                    numOpenReferences = listOfOpenTaskIds.size();
                }
                ArrayList<String> listOfCompletedTaskIds = (ArrayList<String>) userData.get
                        ("completedTaskIds");


                for (int i = maxTasksAssigned; i > numOpenReferences; i--) {
                    // check if we can create any more ratings from database - is there anything
                    // left?
                    // if not, then break
                    // create open rating for this user
                    // add reference to particular open rating for this user
                    // add reference to a task for this rating
                }

                // Get updated user document.
                user = userFuture.get();
                userData = user.getData();
                listOfOpenTaskIds = (ArrayList<String>) userData.get("openTaskIds");

                ArrayList<Object> listOfTasks = new ArrayList<>();
                for (String taskId : listOfOpenTaskIds) {
                    Map<String, Object> taskMap = _database.collection("clientWebSimulator")
                            .document("agent-dialogue-experiments").collection("tasks").document
                                    (taskId).get().get().getData();
                    JsonObject taskJson = new JsonObject();
                    taskJson.addProperty("clientId", (String) taskMap.get("clientId"));
                    taskJson.addProperty("deviceType", (String) taskMap.get("deviceType"));
                    taskJson.addProperty("language_code", (String) taskMap.get("language_code"));
                    taskJson.addProperty("turns", ((ArrayList<Object>) taskMap.get("turns")).toString());
                    listOfTasks.add(taskJson.toString());
                }
                json.addProperty("tasks", listOfTasks.toString());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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

    private Timestamp getTimeStamp() {
        return Timestamp.newBuilder()
                .setSeconds(Instant.now()
                        .getEpochSecond())
                .setNanos(Instant.now()
                        .getNano())
                .build();
    }
}
