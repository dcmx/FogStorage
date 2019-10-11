package com.fonkwill.fogstorage.client.scenario;

import com.fonkwill.fogstorage.client.shared.domain.MeasurementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ResultContainer {

    private static final Logger logger = LoggerFactory.getLogger(ResultContainer.class);

    private static final String CSV_DELIMITER = ";";

    private List<String> variables;

    private Map<String, MeasurementResult> measurementResultMap = new HashMap<>();

    private Map<String, Set<MeasurementResult>> uploadMeasurementResultMap = new HashMap<>();
    private Map<String, Set<MeasurementResult>> downloadMeasurementResultMap = new HashMap<>();


    private  List<List<String>> singleUploadResults = new ArrayList<>();

    private  List<List<String>> singleDownloadResults = new ArrayList<>();

    private String fileNamePath;

    private String resultsFilePath = "./results.csv";

    private FileWriter fileWriter;

    public ResultContainer(String scenarioFile) {
        this.fileNamePath = scenarioFile + "_result.csv";
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void addMeasurementResult(String title, MeasurementResult measurementResult) {
        measurementResultMap.put(title, measurementResult);
    }

    public void createResultFile() {

        try (FileWriter csvWriter = new FileWriter(fileNamePath, false)) {

            this.fileWriter = csvWriter;
            writeVariables();
            writeEmptyLines(2);
            write("Average Results");
            writeEmptyLines(1);
            write("Upload");
            writeUploadResults();
            writeEmptyLines(2);
            write("Download");
            writeDownloadResults();
            writeEmptyLines(2);
            write("Single results");
            writeEmptyLines(1);
            write("Upload");
            writeSingleUploadResults();
            writeEmptyLines(2);
            write("Download");
            writeSingleDownloadResults();
        } catch (IOException e) {
            logger.error("Could not write csv result file");
        }

    }

    private void writeSingleDownloadResults() throws IOException {
        writeOut(this.singleDownloadResults);
    }

    private void writeSingleUploadResults() throws IOException {
        writeOut(this.singleUploadResults);
    }

    private void write(String content) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        rows.add(Arrays.asList(content));
        writeOut(rows);
    }



    private void writeUploadResults() throws IOException {
        this.singleUploadResults = writeOutResults(uploadMeasurementResultMap);
    }

    private void writeDownloadResults() throws IOException {
        this.singleDownloadResults = writeOutResults(downloadMeasurementResultMap);
    }

    private List<List<String>> writeOutResults(Map<String, Set<MeasurementResult>> downloadMeasurementResultMap) throws IOException {
        List<List<String>> contentRows = new ArrayList<>();
        List<List<String>> fogNodeTransferTimeRows = new ArrayList<>();

        List<String> usedStorageNodes = new ArrayList<>();
        List<String> usedFogNodes = new ArrayList<>();

        List<String> contentLine;
        int contentRowMaxSize = 0;
        for (Map.Entry<String, Set<MeasurementResult>> entry : downloadMeasurementResultMap.entrySet()) {
            for (MeasurementResult measurementResult : entry.getValue()) {


                contentLine = new ArrayList<>(Arrays.asList(entry.getKey(),
                        measurementResult.getTotalTime().toString(),
                        measurementResult.getEnDecryptionTime(),
                        measurementResult.getPlacementCalculationTime(),
                        measurementResult.getCodingTime(),
                        measurementResult.getFromFogTotalTransferTime()));

                ArrayList<String> storageNodeTransferTime = new ArrayList<>();
                for (Map.Entry<String, Long> transferToStorage : measurementResult.getNodesFromFogTransferTime().entrySet()) {
                    storageNodeTransferTime = buildStrings(usedStorageNodes, storageNodeTransferTime, transferToStorage);
                }
                contentLine.addAll(storageNodeTransferTime);

                ArrayList<String> fogNodeTransferTime = new ArrayList<>();
                for (Map.Entry<String, Long> transferFogNode : measurementResult.getThroughFogNodesTotalTime().entrySet()) {
                    fogNodeTransferTime = buildStrings(usedFogNodes, fogNodeTransferTime, transferFogNode);
                }
                ArrayList<String> fogNodeTransferTimeLine = new ArrayList<>(fogNodeTransferTime);
                fogNodeTransferTimeRows.add(fogNodeTransferTimeLine);

                if (contentLine.size() > contentRowMaxSize) {
                    contentRowMaxSize = contentLine.size();
                }
                contentRows.add(contentLine);
            }
        }


        List<List<String>> rows = new ArrayList<>();
        List<String> headLine = new ArrayList<>(Arrays.asList("Title", "Total", "EnDecryption", "Placement Calculation", "Coding","Storage transfer"));

        headLine.addAll(usedStorageNodes.stream().map(storageNode ->  "Storage:" + storageNode).collect(Collectors.toList()));
        headLine.addAll(usedFogNodes.stream().map(fogNode -> "Fog:" + fogNode).collect(Collectors.toList()));

        rows.add(headLine);
        if (contentRows.size() != fogNodeTransferTimeRows.size()) {
            throw new IOException("Could not write out file - wrong setup");
        }

        int i = 0;
        for (List<String> contentRow : contentRows) {
            while (contentRow.size() < contentRowMaxSize) {
               contentRow.add("");
            }
            contentRow.addAll(fogNodeTransferTimeRows.get(i));
            i++;
        }

        List<List<String>> singleResults = contentRows;

        contentRows = calculateAverage(contentRows, headLine.size());

        rows.addAll(contentRows);

        writeOut(rows);

        return  singleResults;
    }

    private List<List<String>> calculateAverage(List<List<String>> contentRows, int headLineSize) {
        List<List<String>> result = new ArrayList<>();

        String lastTitle = "";
        int countForTitle = 0;
        List<Long> sumsForTitle = new ArrayList<>();
        for (List<String> contentRow : contentRows) {
            String title = contentRow.get(0);
            if (!title.equals(lastTitle)) {
                if (!lastTitle.isEmpty()){
                    List<String> averageForLastTitle = makeAverageForLastTitle(lastTitle, sumsForTitle, countForTitle);
                    result.add(averageForLastTitle);
                }
                lastTitle = title;
                countForTitle = 0;
                sumsForTitle = new ArrayList<>();
                for (int i = 1;  i < headLineSize; i ++){
                    sumsForTitle.add(0L);
                }
            }
            countForTitle++;
            for (int i = 0;  i < headLineSize-1; i ++){
                Long sum = sumsForTitle.get(i);
                if (i+1 < contentRow.size()){
                    String value = contentRow.get(i+1);
                    Long longValue = value == null || value.isEmpty() ? 0 : Long.valueOf(value);
                    sum += longValue;
                    sumsForTitle.set(i, sum);
                }
            }
        }
        List<String> averageForLastTitle = makeAverageForLastTitle(lastTitle, sumsForTitle, countForTitle);
        result.add(averageForLastTitle);
        return result;

    }

    private List<String> makeAverageForLastTitle(String title, List<Long> sumsForTitle, int countForTitle) {
        List<String> result = new ArrayList<>();
        result.add(title);
        for (Long sum : sumsForTitle) {
            Long avg = sum / countForTitle;
            result.add(avg.toString());
        }
        return result;
    }

    private ArrayList<String> buildStrings(List<String> usedStorageNodes, ArrayList<String> storageNodeTransferTime, Map.Entry<String, Long> transferToStorage) {
        String node = transferToStorage.getKey();
        Long transferTime = transferToStorage.getValue();

        int pos = usedStorageNodes.indexOf(node);
        if (pos == -1) {
            usedStorageNodes.add(node);
            pos = usedStorageNodes.size() - 1;
        }
        storageNodeTransferTime = setOnPosition(storageNodeTransferTime, pos, transferTime);
        return storageNodeTransferTime;
    }

    private ArrayList<String> setOnPosition(ArrayList<String> storageNodeTransferTime, int pos, Long transferTime) {
        int currentSize = storageNodeTransferTime.size();
        if(currentSize-1 < pos) {
            while (currentSize < pos) {
                storageNodeTransferTime.add("");
                currentSize++;
            }
            storageNodeTransferTime.add(transferTime.toString());
        } else {
            storageNodeTransferTime.set(pos, transferTime.toString());
        }
        return storageNodeTransferTime;

    }

    private void writeOut(List<List<String>> rows) throws IOException {
        for (List<String> rowData : rows) {
            fileWriter.append(String.join(CSV_DELIMITER, rowData));
            fileWriter.append("\n");
        }
    }

    private void writeEmptyLines(int i) throws IOException {
        for (int x = 0; x<=i; x++) {
            this.fileWriter.append("\n");
        }
    }

    private void writeVariables() throws IOException {
        List<List<String>> rows = new ArrayList<>();
        List<String> headLine = Arrays.asList("Parameter", "Value");
        rows.add(headLine);

        List<String> contentLine;
        for (String variable : variables) {
            int colon = variable.indexOf(":");
            String key = variable.substring(0, colon).replace("{", "").replace("}","");
            String value  = variable.substring(colon+1);
            contentLine = Arrays.asList(key, value);
            rows.add(contentLine);
        }

        writeOut(rows);


    }

    public void addUploadMeasurmentResult(String title, MeasurementResult measurementResult) {
        try (FileWriter csvWriter = new FileWriter(resultsFilePath, true)) {
            List<String> rowData = new ArrayList<>();
            rowData.add("U");
            rowData.add(title);
            Long fileSizeInBytes = measurementResult.getFileSize();
            Long fileSizeInMB = fileSizeInBytes / 1024 / 1024;
            rowData.add(fileSizeInMB.toString());
            rowData.add(measurementResult.getTotalTime().toString());
            csvWriter.append(String.join(CSV_DELIMITER, rowData));
            csvWriter.append("\n");
        } catch (IOException e) {
            logger.error("Could not write to results file", e);
        }

        addToMap(title, measurementResult, uploadMeasurementResultMap);
    }

    public void addDownloadMeasurmentResult(String title, MeasurementResult measurementResult) {

        try (FileWriter csvWriter = new FileWriter(resultsFilePath, true)) {
            List<String> rowData = new ArrayList<>();
            rowData.add("D");
            rowData.add(title);
            Long fileSizeInBytes = measurementResult.getFileSize();
            Long fileSizeInMB = fileSizeInBytes / 1024 / 1024;
            rowData.add(fileSizeInMB.toString());
            rowData.add(measurementResult.getTotalTime().toString());
            csvWriter.append(String.join(CSV_DELIMITER, rowData));
            csvWriter.append("\n");
        } catch (IOException e) {
            logger.error("Could not write to results file", e);
        }
        addToMap(title, measurementResult, downloadMeasurementResultMap);
    }

    private void addToMap(String title, MeasurementResult measurementResult, Map<String, Set<MeasurementResult>> downloadMeasurementResultMap) {
        Set<MeasurementResult> storedSet = downloadMeasurementResultMap.get(title);
        if (storedSet == null) {
            storedSet = new HashSet<>();
        }
        storedSet.add(measurementResult);
        downloadMeasurementResultMap.put(title, storedSet);
    }


}
