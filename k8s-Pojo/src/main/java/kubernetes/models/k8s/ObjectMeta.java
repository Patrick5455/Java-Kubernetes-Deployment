package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
@JsonDeserialize
public class ObjectMeta {
    private String name;
    private String UID;
    private String namespace;
    private HashMap<String, String> labels;



}
