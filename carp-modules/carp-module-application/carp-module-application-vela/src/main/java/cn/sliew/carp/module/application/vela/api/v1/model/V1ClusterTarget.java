package cn.sliew.carp.module.application.vela.api.v1.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * V1ClusterTarget
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2024-08-01T20:13:23.593+08:00")


public class V1ClusterTarget   {
  @JsonProperty("clusterName")
  private String clusterName = null;

  @JsonProperty("namespace")
  private String namespace = null;

  public V1ClusterTarget clusterName(String clusterName) {
    this.clusterName = clusterName;
    return this;
  }

  /**
   * Get clusterName
   * @return clusterName
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public V1ClusterTarget namespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

  /**
   * Get namespace
   * @return namespace
  **/
  @ApiModelProperty(value = "")


  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    V1ClusterTarget v1ClusterTarget = (V1ClusterTarget) o;
    return Objects.equals(this.clusterName, v1ClusterTarget.clusterName) &&
        Objects.equals(this.namespace, v1ClusterTarget.namespace);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clusterName, namespace);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class V1ClusterTarget {\n");
    
    sb.append("    clusterName: ").append(toIndentedString(clusterName)).append("\n");
    sb.append("    namespace: ").append(toIndentedString(namespace)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

