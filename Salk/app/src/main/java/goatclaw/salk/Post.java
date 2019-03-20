package goatclaw.salk;

import java.lang.String;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
    Esta es la clase que implementa la respuesta que recibimos de la API.
    Expose y SerializedName son directivas que permiten a retrofit serializar
    y mapear el json que recibimos de la API con los atributos de la clase
 */
public class Post {

    @SerializedName("ok")
    @Expose
    private String ok;

    public String getPrediction() {
        return ok;
    }

    public void setPrediction(String prediction) {
        this.ok = prediction;
    }

    @Override
    public String toString() {
        return "Post{" +
                "prediction='" + ok + '}';
    }
}
