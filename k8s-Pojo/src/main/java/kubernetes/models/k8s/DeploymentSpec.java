package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonDeserialize
public class DeploymentSpec {
    private int minReadySeconds;
    private boolean paused;
//    private int progressDeadlineSeconds;
    private int replicas;
    private int revisionHistoryLimit;
    private LabelSelector selector;
    private DeploymentStrategy strategy;
    private PodTemplateSpec template;
    private List<Container> containers;
    private Container container;


}
