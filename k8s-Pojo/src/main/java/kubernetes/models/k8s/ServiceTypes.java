package kubernetes.models.k8s;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;



@JsonDeserialize
public enum ServiceTypes {
    LoadBalancer,
    ClusterIp;
}
