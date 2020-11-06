package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonDeserialize
public class Container {

    private String image;
    private String name;
    private ContainerPort port;
    private List<ContainerPort> ports;

//    private ArrayList<Container> containers;

}
