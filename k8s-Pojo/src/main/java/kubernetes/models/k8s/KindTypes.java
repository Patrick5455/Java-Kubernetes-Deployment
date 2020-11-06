package kubernetes.models.k8s;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public enum KindTypes {

    ConfigMap,
    Service,
    Deployment,
    Job,
    Pod,
    ReplicationController,
    Namespace,
    Node,
    PodLists,
    ServiceLists,
    NodeLists,
    Status,
    Binding;
}
