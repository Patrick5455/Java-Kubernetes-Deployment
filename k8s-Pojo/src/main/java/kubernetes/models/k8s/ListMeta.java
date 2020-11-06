package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize
public class ListMeta {
    // This K8s field conflicts java continue keyword so at runtime,
    //we would use regex to remove non alphanumeric keys when writing to file
    private String continue_;
    private int remainingItemCount;
    private String resourceVersion;
    private String selfLink;

}
