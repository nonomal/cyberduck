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
 * Upload channel information
 */
@ApiModel(description = "Upload channel information")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class CreateShareUploadChannelResponse {
  @JsonProperty("uploadId")
  private String uploadId = null;

  @JsonProperty("uploadUrl")
  private String uploadUrl = null;

  @JsonProperty("token")
  private String token = null;

  public CreateShareUploadChannelResponse uploadId(String uploadId) {
    this.uploadId = uploadId;
    return this;
  }

   /**
   * Upload (channel) ID
   * @return uploadId
  **/
  @ApiModelProperty(required = true, value = "Upload (channel) ID")
  public String getUploadId() {
    return uploadId;
  }

  public void setUploadId(String uploadId) {
    this.uploadId = uploadId;
  }

  public CreateShareUploadChannelResponse uploadUrl(String uploadUrl) {
    this.uploadUrl = uploadUrl;
    return this;
  }

   /**
   * (public) Upload URL
   * @return uploadUrl
  **/
  @ApiModelProperty(example = "https://www.random-url.com", required = true, value = "(public) Upload URL")
  public String getUploadUrl() {
    return uploadUrl;
  }

  public void setUploadUrl(String uploadUrl) {
    this.uploadUrl = uploadUrl;
  }

  public CreateShareUploadChannelResponse token(String token) {
    this.token = token;
    return this;
  }

   /**
    * &#x60;DEPRECATED&#x60;: Upload token  [Deprecated since version 4.3.0]
   * @return token
  **/
   @ApiModelProperty(value = "`DEPRECATED`: Upload token  [Deprecated since version 4.3.0]")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateShareUploadChannelResponse createShareUploadChannelResponse = (CreateShareUploadChannelResponse) o;
    return Objects.equals(this.uploadId, createShareUploadChannelResponse.uploadId) &&
        Objects.equals(this.uploadUrl, createShareUploadChannelResponse.uploadUrl) &&
        Objects.equals(this.token, createShareUploadChannelResponse.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uploadId, uploadUrl, token);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateShareUploadChannelResponse {\n");

      sb.append("    uploadId: ").append(toIndentedString(uploadId)).append("\n");
    sb.append("    uploadUrl: ").append(toIndentedString(uploadUrl)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

