package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonDeserialize
public class PodSpec {

    //private int activeDeadlineSeconds=2147483647;
    private boolean automountServiceAccountToken;
    private String dnsPolicy;
    private boolean enableServiceLinks;
    private boolean hostIPC;
    private boolean hostNetwork;
    private boolean hostPID;
    private String hostname;
    private String nodeName;
    private String preemptionPolicy;
    private int priority;
    private String priorityClassName;
    private String restartPolicy;

    private String runtimeClassName;
    private String schedulerName;
    private String serviceAccount;
    private String serviceAccountName;
    private boolean setHostnameAsFQDN;
    private boolean shareProcessNamespace;

    private String subdomain;
    private int terminationGracePeriodSeconds;

    private List<Container> containers;

    //TODO: add fields of reference types (Other classes)


}
