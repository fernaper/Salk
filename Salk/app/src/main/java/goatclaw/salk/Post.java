package goatclaw.salk;

import java.lang.String;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("prediction")
    @Expose
    private String prediction;
    @SerializedName("confidence")
    @Expose
    private Double confidence;

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    @Override
    public String toString() {
        return "Post{" +
                "prediction='" + prediction + '\'' +
                ", confidence=" + confidence +
                '}';
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

}
