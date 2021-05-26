package com.aws.learning.model;

public class AgentModel {

    private String bucket;
    private String folder;
    private String data;
    private String type;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AgentModel{" +
                "bucket='" + bucket + '\'' +
                ", folder='" + folder + '\'' +
                ", data='" + data + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
