package edu.gla.kail.ad.uploader;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

class ExperimentHandler {

    void handleExperiments(BufferedReader tsvFileBufferedReader, ArrayList<String>
            arrayOfParameters, Firestore database) throws IOException {
        StringTokenizer stringTokenizer;
        String nextRow = tsvFileBufferedReader.readLine();

        while (nextRow != null) {
            stringTokenizer = new StringTokenizer(nextRow, "\t");
            Map<String, Object> updateHelperMap = new HashMap<>();
            ArrayList<String> dataArray = new ArrayList<>();

            while (stringTokenizer.hasMoreElements()) {
                dataArray.add(stringTokenizer.nextElement().toString());
            }

            for (int i = 0; i < dataArray.size(); i++) {
                updateHelperMap.put(arrayOfParameters.get(i), dataArray.get(i));
            }

            // If line is empty, then read next line and continue executing while loop.
            if (updateHelperMap.size() == 0) {
                nextRow = tsvFileBufferedReader.readLine();
                continue;
            }

            DocumentReference experimentDocRef = database
                    .collection("clientWebSimulator")
                    .document("agent-dialogue-experiments")
                    .collection("experiments")
                    .document(updateHelperMap.get("experimentId").toString());
            experimentDocRef.set(updateHelperMap);
            nextRow = tsvFileBufferedReader.readLine();
        }
    }
}
