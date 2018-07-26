package edu.gla.kail.ad.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.Timestamp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
                Long startTime_seconds = Long.valueOf(request.getParameter
                        ("startTime_seconds"));
                Long endTime_seconds = Long.valueOf(request.getParameter("endTime_seconds"));
                rateTask(userId, ratingScore, taskId, startTime_seconds, endTime_seconds);
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

        DocumentReference userDocRef = _database.collection("clientWebSimulator").document
                ("agent-dialogue-experiments").collection("users").document(userId);
        ApiFuture<DocumentSnapshot> userFuture = userDocRef.get();
        try {
            DocumentSnapshot user = userFuture.get();
            if (user.exists()) {
                Map<String, Object> userData = user.getData();
                ArrayList<String> listOfOpenTaskIds = (ArrayList<String>) userData.get
                        ("openTaskIds");
                int numOpenRatings = 0;
                if (listOfOpenTaskIds == null) {
                    listOfOpenTaskIds = new ArrayList<>();
                }
                numOpenRatings = listOfOpenTaskIds.size();
                ArrayList<String> listOfCompletedTaskIds = (ArrayList<String>) userData.get
                        ("completedTaskIds");
                HashSet hashsetCompletedTaskIds = new HashSet(listOfCompletedTaskIds);
                hashsetCompletedTaskIds.addAll(listOfOpenTaskIds);

                // Do if the user can have more open tasks.
                if (numOpenRatings <= maxTasksAssigned) {
                    HashSet<String> allPossibleTasks = new HashSet<>();

                    // Get all candidate tasks.
                    ApiFuture<QuerySnapshot> tasksFuture = _database.collection
                            ("clientWebSimulator").document("agent-dialogue-experiments")
                            .collection("ratings").get();
                    List<QueryDocumentSnapshot> tasks = tasksFuture.get().getDocuments();
                    for (DocumentSnapshot task : tasks) {
                        allPossibleTasks.add((String) task.get("taskId"));
                    }
                    // Leave only not completed and not open tasks.
                    allPossibleTasks.removeAll(hashsetCompletedTaskIds);

                    for (int i = maxTasksAssigned; i > numOpenRatings; i--) {
                        // Check if we can create any more ratings.
                        if (allPossibleTasks.size() <= 0) {
                            break;
                        }
                        String newRatingTaskId = allPossibleTasks.iterator().next();
                        listOfOpenTaskIds.add(newRatingTaskId);

                        // Create open rating for this user.
                        String createdRatingId = newRatingTaskId + "_" + userId;
                        DocumentReference createdRatingDocRef = _database.collection
                                ("clientWebSimulator").document("agent-dialogue-experiments")
                                .collection("ratings").document(createdRatingId);
                        Map<String, Object> data = new HashMap<>();
                        Timestamp timeNow = getTimeStamp();
                        data.put("assignTime_nanos", timeNow.getNanos());
                        data.put("assignTime_seconds", timeNow.getSeconds());
                        data.put("complete", false);
                        data.put("taskId", newRatingTaskId);
                        data.put("userId", userId);

                        createdRatingDocRef.set(data);

                        // Add this taskId to openTaskIds list in the User Document.
                        Map<String, Object> updatingHelperMap = new HashMap<>();
                        updatingHelperMap.put("openTaskIds", listOfOpenTaskIds);
                        userDocRef.update(updatingHelperMap);

                        // Add this ratingId to ratingIds list in the Task Document.
                        DocumentReference taskDocRef = _database.collection("clientWebSimulator")
                                .document("agent-dialogue-experiments")
                                .collection("tasks").document(newRatingTaskId);
                        ArrayList<String> ratingsIds = (ArrayList<String>) taskDocRef.get().get()
                                .getData().get("ratingsIds");
                        if (ratingsIds == null) {
                            ratingsIds = new ArrayList<>();
                        }
                        ratingsIds.add(createdRatingId);
                        updatingHelperMap = new HashMap<>();
                        updatingHelperMap.put("ratingIds", ratingsIds);
                        taskDocRef.update(updatingHelperMap);
                        allPossibleTasks.remove(newRatingTaskId);
                    }
                    // Get updated user document.
                    userData = userDocRef.get().get().getData();
                    listOfOpenTaskIds = (ArrayList<String>) userData.get("openTaskIds");
                }


                JsonObject jsonOfTasks = new JsonObject();
                for (Integer taskIndex = 0; taskIndex < listOfOpenTaskIds.size(); taskIndex++) {
                    String taskId = listOfOpenTaskIds.get(taskIndex);
                    Map<String, Object> taskMap = _database.collection("clientWebSimulator")
                            .document("agent-dialogue-experiments").collection("tasks").document
                                    (taskId).get().get().getData();
                    JsonObject taskJson = new JsonObject();
                    taskJson.addProperty("clientId", (String) taskMap.get("clientId"));
                    taskJson.addProperty("deviceType", (String) taskMap.get("deviceType"));
                    taskJson.addProperty("taskId", (String) taskMap.get("taskId"));
                    taskJson.addProperty("language_code", (String) taskMap.get("language_code"));
                    JsonObject jsonTurns = new JsonObject();
                    ArrayList<Object> taskTurns = ((ArrayList<Object>) taskMap.get("turns"));
                    for (Integer turnIndex = 0; turnIndex < taskTurns.size(); turnIndex++) {
                        jsonTurns.addProperty(turnIndex.toString(), (new Gson().toJson(taskTurns
                                .get(turnIndex))));
                    }
                    taskJson.addProperty("turns", jsonTurns.toString());
                    jsonOfTasks.addProperty(taskIndex.toString(), taskJson.toString());
                }
                json.addProperty("tasks", jsonOfTasks.toString());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


    private void rateTask(String userId, Integer ratingScore, String taskId, Long
            startTime_seconds, Long endTime_seconds) {
        // Update rating in the ratings collection.
        String ratingId = taskId + "_" + userId;
        DocumentReference ratingDocRef = _database.collection("clientWebSimulator").document
                ("agent-dialogue-experiments").collection("ratings").document(ratingId);
        Map<String, Object> updateHelperMap = new HashMap<>();
        updateHelperMap.put("ratingScore", ratingScore);
        updateHelperMap.put("startTime_seconds", startTime_seconds);
        updateHelperMap.put("endTime_seconds", endTime_seconds);
        updateHelperMap.put("totalTime_seconds", endTime_seconds - startTime_seconds);
        updateHelperMap.put("complete", true);
        ratingDocRef.update(updateHelperMap);

        // Update user database - remove task ID from list of open tasks in User document.
        // Add rated task to the list of completed tasks.
        DocumentReference userDocRef = _database.collection("clientWebSimulator").document
                ("agent-dialogue-experiments").collection("users").document(userId);
        try {
            ArrayList<String> listOfOpenTaskIds = (ArrayList<String>) userDocRef.get().get()
                    .getData().get("openTaskIds");
            ArrayList<String> completedTaskIds = (ArrayList<String>) userDocRef.get().get()
                    .getData().get("completedTaskIds");
            updateHelperMap.clear();
            if (listOfOpenTaskIds.remove(taskId)) {
                updateHelperMap.put("openTaskIds", listOfOpenTaskIds);
            }
            if (!completedTaskIds.contains(taskId)) {
                completedTaskIds.add(taskId);
                updateHelperMap.put("completedTaskIds", completedTaskIds);
            }
            if (!updateHelperMap.isEmpty()) {
                userDocRef.update(updateHelperMap);
            }
        } catch (InterruptedException | ExecutionException exception) {
            // TODO(Adam): Handle this.
        }

        // Update task document
        DocumentReference taskDocRef = _database.collection("clientWebSimulator").document
                ("agent-dialogue-experiments").collection("tasks").document(taskId);
        try {
            ArrayList<String> listOfRatings = (ArrayList<String>) taskDocRef.get().get()
                    .getData().get("ratingIds");
            Long numberOfRemainingRatings = (Long) taskDocRef.get().get()
                    .getData().get("numberOfRemainingRatings");
            if (!listOfRatings.contains(ratingId)) {
                listOfRatings.add(ratingId);
                numberOfRemainingRatings -= 1;
                updateHelperMap.clear();
                updateHelperMap.put("ratingIds", listOfRatings);
                updateHelperMap.put("numberOfRemainingRatings", numberOfRemainingRatings);
                taskDocRef.update(updateHelperMap);
            }
        } catch (InterruptedException | ExecutionException exception) {
            // TODO(Adam): Handle this.
        }
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
