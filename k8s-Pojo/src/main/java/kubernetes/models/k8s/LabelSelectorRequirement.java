package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
@JsonDeserialize
public class LabelSelectorRequirement {
    private String key;
    private String operator;
    private ArrayList<String> values;
}
