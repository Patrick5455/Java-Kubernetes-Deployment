package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
@JsonDeserialize
public class DeploymentListYaml {

    private String apiVersion;
    private List<DeploymentYaml> items;
    private KindTypes kind;
    private ListMeta metadata;


}
