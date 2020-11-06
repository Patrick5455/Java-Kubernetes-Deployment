package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
@JsonDeserialize
public class LabelSelector {

    private LabelSelectorRequirement matchExpressions;
    private HashMap<String, String> matchLabels;
    private HashMap<String, String> labels;




}
