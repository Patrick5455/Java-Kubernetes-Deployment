package kubernetes.models.k8s;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize
public class DeploymentYaml {

    private String apiVersion;
    private KindTypes kind;
    private ObjectMeta metadata;
    private DeploymentSpec spec;


}
