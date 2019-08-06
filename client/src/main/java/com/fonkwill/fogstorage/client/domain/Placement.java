package com.fonkwill.fogstorage.client.domain;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Placement {

    private List<Assignment> storedAtList = new ArrayList<>();

    private PlacementStrategy placementStrategy;

    public FileInfo getFileInfo() {
        return originalFileInfo;
    }

    public void setOriginalFileInfo(FileInfo originalFileInfo) {
        this.originalFileInfo = originalFileInfo;
    }

    private FileInfo originalFileInfo;

    public List<Assignment> getStoredAtList() {
        return storedAtList;
    }

    public void setStoredAtList(List<Assignment> storedAtList) {
        this.storedAtList = storedAtList;
    }

    public PlacementStrategy getPlacementStrategy() {
        return placementStrategy;
    }

    public void setPlacementStrategy(PlacementStrategy placementStrategy) {
        this.placementStrategy = placementStrategy;
    }

    public static class Assignment{

        Chunk chunk;

        Node node;

        public Chunk getChunk() {
            return chunk;
        }

        public void setChunk(Chunk chunk) {
            this.chunk = chunk;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }



    }
}
