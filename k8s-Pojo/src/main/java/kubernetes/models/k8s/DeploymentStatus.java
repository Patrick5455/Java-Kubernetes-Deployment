package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize
/**
 * This is different from DeploymentStatus of type Enum
 * **/
public class DeploymentStatus {
    private int availableReplicas;
    private int collisionCount;
    private int observedGeneration;
    private int readyReplicas;
    private int unavailableReplicas;
    private int updatedReplicas;

    /* TODO: Add fields of object type
    *   DeploymentCondition
    *
    * */

}