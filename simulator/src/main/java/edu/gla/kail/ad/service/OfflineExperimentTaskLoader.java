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

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class OfflineExperimentTaskLoader {
    private Firestore _database;
    private String _userId;

    /**
     * @param database
     * @param userId
     * @param maxNumberOfTasksAssigned
     * @return
     */
    String loadTasks(Firestore database, String userId, Integer maxNumberOfTasksAssigned) {
        _database = database;
        _userId = userId;
        JsonObject json = new JsonObject();

        DocumentReference userDocRef = _database.collection("clientWebSimulator").document
                ("agent-dialogue-experiments").collection("users").document(_userId);
        try {
            DocumentSnapshot user = userDocRef.get().get();
            if (user.exists()) {
                Map<String, Object> userData = user.getData();
                ArrayList<String> listOfOpenTaskIds;

                if (userData.containsKey("openTaskIds")) {
                    listOfOpenTaskIds = (ArrayList<String>) userData.get("openTaskIds");
                } else {
                    listOfOpenTaskIds = new ArrayList<>();
                }
                int numberOfOpenRatings = listOfOpenTaskIds.size();
                ArrayList<String> listOfCompletedTaskIds;

                if (userData.containsKey("completedTaskIds")) {
                    listOfCompletedTaskIds = (ArrayList<String>) userData.get("completedTaskIds");
                } else {
                    listOfCompletedTaskIds = new ArrayList<>();
                }
                HashSet allAssignedTaskIds = new HashSet(listOfCompletedTaskIds);

                allAssignedTaskIds.addAll(listOfOpenTaskIds);

                // Assign more tasks to the user if the user can have more open tasks and there
                // are some more available.
                if (numberOfOpenRatings <= maxNumberOfTasksAssigned) {
                    listOfOpenTaskIds = assignMoreTasksToUser(allAssignedTaskIds,
                            listOfOpenTaskIds, maxNumberOfTasksAssigned, numberOfOpenRatings, userDocRef);
                }
                json.addProperty("tasks", getOpenTasks(listOfOpenTaskIds));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private ArrayList<String> assignMoreTasksToUser(HashSet<String> allAssignedTaskIds, ArrayList<String>
            listOfOpenTaskIds, Integer maxNumberOfTasksAssigned, Integer numberOfOpenRatings, DocumentReference userDocRef)
            throws ExecutionException, InterruptedException {
        HashSet<String> remainingAvailableTasks = new HashSet<>();

        // Get all candidate tasks.
        ApiFuture<QuerySnapshot> tasksFuture = _database.collection
                ("clientWebSimulator").document("agent-dialogue-experiments")
                .collection("ratings").get();
        List<QueryDocumentSnapshot> tasks = tasksFuture.get().getDocuments();
        for (DocumentSnapshot task : tasks) {
            remainingAvailableTasks.add((String) task.get("taskId"));
        }
        // Leave only not completed and not open tasks.
        remainingAvailableTasks.removeAll(allAssignedTaskIds);

        for (int i = maxNumberOfTasksAssigned; i > numberOfOpenRatings; i--) {
            // Check if we can create any more ratings.
            if (remainingAvailableTasks.size() <= 0) {
                break;
            }
            String createdTaskId = remainingAvailableTasks.iterator().next();
            listOfOpenTaskIds.add(createdTaskId);

            // Create open rating for this user.
            String createdRatingId = createNewOpenRating(createdTaskId);

            // Add this ratingId to ratingIds list in the Task Document.
            updateTaskDocument(createdTaskId, createdRatingId);

            remainingAvailableTasks.remove(createdTaskId);
        }

        // Add this taskId to openTaskIds list in the User Document.
        Map<String, Object> helperMap = new HashMap<>();
        helperMap.put("openTaskIds", listOfOpenTaskIds);
        userDocRef.update(helperMap);

        // Get updated user document.
        Map<String, Object> userData = userDocRef.get().get().getData();
        listOfOpenTaskIds = (ArrayList<String>) userData.get("openTaskIds");
        return listOfOpenTaskIds;
    }

    private void updateTaskDocument(String taskId, String ratingId) throws ExecutionException,
            InterruptedException {
        DocumentReference taskDocRef = _database.collection("clientWebSimulator")
                .document("agent-dialogue-experiments")
                .collection("tasks").document(taskId);
        ArrayList<String> ratingsIds = (ArrayList<String>) taskDocRef.get().get()
                .getData().get("ratingsIds");
        if (ratingsIds == null) {
            ratingsIds = new ArrayList<>();
        }
        ratingsIds.add(ratingId);
        HashMap<String, Object> helperMap = new HashMap<>();
        helperMap.put("ratingIds", ratingsIds);
        taskDocRef.update(helperMap);
    }

    private String createNewOpenRating(String taskId) {
        String ratingId = taskId + "_" + _userId;
        DocumentReference createdRatingDocRef = _database.collection
                ("clientWebSimulator").document("agent-dialogue-experiments")
                .collection("ratings").document(ratingId);
        Map<String, Object> data = new HashMap<>();
        Timestamp timeNow = getTimeStamp();
        data.put("ratingId", ratingId);
        data.put("assignTime_nanos", timeNow.getNanos());
        data.put("assignTime_seconds", timeNow.getSeconds());
        data.put("complete", false);
        data.put("taskId", taskId);
        data.put("userId", _userId);

        createdRatingDocRef.set(data);
        return ratingId;
    }

    private String getOpenTasks(ArrayList<String> listOfOpenTaskIds) throws ExecutionException,
            InterruptedException {
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
        return jsonOfTasks.toString();
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
