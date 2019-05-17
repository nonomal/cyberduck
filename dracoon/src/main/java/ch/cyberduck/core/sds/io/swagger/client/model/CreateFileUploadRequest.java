/*
 * DRACOON
 * REST Web Services for DRACOON<br>Version: 4.11.2  - built at: 2019-05-03 12:34:42<br><br><a title='Developer Information' href='https://developer.dracoon.com'>Developer Information</a>&emsp;&emsp;<a title='Get SDKs on GitHub' href='https://github.com/dracoon'>Get SDKs on GitHub</a>
 *
 * OpenAPI spec version: 4.11.2
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package ch.cyberduck.core.sds.io.swagger.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Request model for creating an upload channel
 */
@ApiModel(description = "Request model for creating an upload channel")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class CreateFileUploadRequest {
  @JsonProperty("parentId")
  private Long parentId = null;

  @JsonProperty("name")
  private String name = null;

  /**
   * Classification ID: * &#x60;1&#x60; - public * &#x60;2&#x60; - internal * &#x60;3&#x60; - confidential * &#x60;4&#x60; - strictly confidential  (default: classification from parent room)
   */
  public enum ClassificationEnum {
    NUMBER_1(1),
    
    NUMBER_2(2),
    
    NUMBER_3(3),
    
    NUMBER_4(4);

    private Integer value;

    ClassificationEnum(Integer value) {
      this.value = value;
    }

    @JsonValue
    public Integer getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ClassificationEnum fromValue(String text) {
      for (ClassificationEnum b : ClassificationEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("classification")
  private ClassificationEnum classification = null;

  @JsonProperty("size")
  private Long size = null;

  @JsonProperty("expiration")
  private ObjectExpiration expiration = null;

  @JsonProperty("notes")
  private String notes = null;

  public CreateFileUploadRequest parentId(Long parentId) {
    this.parentId = parentId;
    return this;
  }

   /**
   * Parent node ID (room or folder)
   * @return parentId
  **/
  @ApiModelProperty(required = true, value = "Parent node ID (room or folder)")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public CreateFileUploadRequest name(String name) {
    this.name = name;
    return this;
  }

   /**
   * File name
   * @return name
  **/
  @ApiModelProperty(required = true, value = "File name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CreateFileUploadRequest classification(ClassificationEnum classification) {
    this.classification = classification;
    return this;
  }

   /**
    * Classification ID: * &#x60;1&#x60; - public * &#x60;2&#x60; - internal * &#x60;3&#x60; - confidential * &#x60;4&#x60; - strictly confidential  (default: classification from parent room)
   * @return classification
  **/
   @ApiModelProperty(example = "2", value = "Classification ID: * `1` - public * `2` - internal * `3` - confidential * `4` - strictly confidential  (default: classification from parent room)")
  public ClassificationEnum getClassification() {
    return classification;
  }

  public void setClassification(ClassificationEnum classification) {
    this.classification = classification;
  }

  public CreateFileUploadRequest size(Long size) {
    this.size = size;
    return this;
  }

   /**
   * File size in byte
   * @return size
  **/
  @ApiModelProperty(value = "File size in byte")
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public CreateFileUploadRequest expiration(ObjectExpiration expiration) {
    this.expiration = expiration;
    return this;
  }

   /**
   * Expiration date / time
   * @return expiration
  **/
  @ApiModelProperty(value = "Expiration date / time")
  public ObjectExpiration getExpiration() {
    return expiration;
  }

  public void setExpiration(ObjectExpiration expiration) {
    this.expiration = expiration;
  }

  public CreateFileUploadRequest notes(String notes) {
    this.notes = notes;
    return this;
  }

   /**
   * User notes (limited to 255 characters) Use empty string to remove.
   * @return notes
  **/
  @ApiModelProperty(value = "User notes (limited to 255 characters) Use empty string to remove.")
  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateFileUploadRequest createFileUploadRequest = (CreateFileUploadRequest) o;
    return Objects.equals(this.parentId, createFileUploadRequest.parentId) &&
        Objects.equals(this.name, createFileUploadRequest.name) &&
        Objects.equals(this.classification, createFileUploadRequest.classification) &&
        Objects.equals(this.size, createFileUploadRequest.size) &&
        Objects.equals(this.expiration, createFileUploadRequest.expiration) &&
        Objects.equals(this.notes, createFileUploadRequest.notes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentId, name, classification, size, expiration, notes);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateFileUploadRequest {\n");

      sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    classification: ").append(toIndentedString(classification)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
    sb.append("    notes: ").append(toIndentedString(notes)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

