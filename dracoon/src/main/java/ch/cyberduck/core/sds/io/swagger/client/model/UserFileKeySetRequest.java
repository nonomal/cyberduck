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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Request model for setting a user file key
 */
@ApiModel(description = "Request model for setting a user file key")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class UserFileKeySetRequest {
  @JsonProperty("fileId")
  private Long fileId = null;

  @JsonProperty("userId")
  private Long userId = null;

  @JsonProperty("fileKey")
  private FileKey fileKey = null;

  public UserFileKeySetRequest fileId(Long fileId) {
    this.fileId = fileId;
    return this;
  }

   /**
   * File ID
   * @return fileId
  **/
  @ApiModelProperty(required = true, value = "File ID")
  public Long getFileId() {
    return fileId;
  }

  public void setFileId(Long fileId) {
    this.fileId = fileId;
  }

  public UserFileKeySetRequest userId(Long userId) {
    this.userId = userId;
    return this;
  }

   /**
   * Unique identifier for the user
   * @return userId
  **/
  @ApiModelProperty(required = true, value = "Unique identifier for the user")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public UserFileKeySetRequest fileKey(FileKey fileKey) {
    this.fileKey = fileKey;
    return this;
  }

   /**
   * User file key
   * @return fileKey
  **/
  @ApiModelProperty(required = true, value = "User file key")
  public FileKey getFileKey() {
    return fileKey;
  }

  public void setFileKey(FileKey fileKey) {
    this.fileKey = fileKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserFileKeySetRequest userFileKeySetRequest = (UserFileKeySetRequest) o;
    return Objects.equals(this.fileId, userFileKeySetRequest.fileId) &&
        Objects.equals(this.userId, userFileKeySetRequest.userId) &&
        Objects.equals(this.fileKey, userFileKeySetRequest.fileKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileId, userId, fileKey);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserFileKeySetRequest {\n");

      sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    fileKey: ").append(toIndentedString(fileKey)).append("\n");
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

