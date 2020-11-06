package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize
public class PodTemplateSpec {

    private PodSpec spec;
    private ObjectMeta metadata;
}
