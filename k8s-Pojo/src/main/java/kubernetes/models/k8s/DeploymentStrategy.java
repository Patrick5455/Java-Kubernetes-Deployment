package kubernetes.models.k8s;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize
public class DeploymentStrategy {

    private String type;
    private RollingUpdateDeployment rollingUpdate;


}
