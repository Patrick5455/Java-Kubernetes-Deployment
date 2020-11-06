package kubernetes.models.k8s;

import java.util.HashMap;

public class LabelUtils {

    public static HashMap<String, String> setAnyLabels(String key, String value){

        HashMap<String, String> labels = new HashMap<>();
        labels.put(key, value);

        return labels;
    }
}
