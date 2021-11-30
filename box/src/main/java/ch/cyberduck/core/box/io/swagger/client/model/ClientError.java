/*
 * Box Platform API
 * [Box Platform](https://box.dev) provides functionality to provide access to content stored within [Box](https://box.com). It provides endpoints for basic manipulation of files and folders, management of users within an enterprise, as well as more complex topics such as legal holds and retention policies.
 *
 * OpenAPI spec version: 2.0.0
 * Contact: devrel@box.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.cyberduck.core.box.io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import ch.cyberduck.core.box.io.swagger.client.model.ClientErrorContextInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * A generic error
 */
@Schema(description = "A generic error")

public class ClientError {
  /**
   * &#x60;error&#x60;
   */
  public enum TypeEnum {
    ERROR("error");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("type")
  private TypeEnum type = null;

  @JsonProperty("status")
  private Integer status = null;

  /**
   * A Box-specific error code
   */
  public enum CodeEnum {
    CREATED("created"),
    ACCEPTED("accepted"),
    NO_CONTENT("no_content"),
    REDIRECT("redirect"),
    NOT_MODIFIED("not_modified"),
    BAD_REQUEST("bad_request"),
    UNAUTHORIZED("unauthorized"),
    FORBIDDEN("forbidden"),
    NOT_FOUND("not_found"),
    METHOD_NOT_ALLOWED("method_not_allowed"),
    CONFLICT("conflict"),
    PRECONDITION_FAILED("precondition_failed"),
    TOO_MANY_REQUESTS("too_many_requests"),
    INTERNAL_SERVER_ERROR("internal_server_error"),
    UNAVAILABLE("unavailable"),
    ITEM_NAME_INVALID("item_name_invalid"),
    INSUFFICIENT_SCOPE("insufficient_scope");

    private String value;

    CodeEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static CodeEnum fromValue(String text) {
      for (CodeEnum b : CodeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("code")
  private CodeEnum code = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("context_info")
  private ClientErrorContextInfo contextInfo = null;

  @JsonProperty("help_url")
  private String helpUrl = null;

  @JsonProperty("request_id")
  private String requestId = null;

  public ClientError type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * &#x60;error&#x60;
   * @return type
  **/
  @Schema(example = "error", description = "`error`")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public ClientError status(Integer status) {
    this.status = status;
    return this;
  }

   /**
   * The HTTP status of the response.
   * @return status
  **/
  @Schema(example = "400", description = "The HTTP status of the response.")
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public ClientError code(CodeEnum code) {
    this.code = code;
    return this;
  }

   /**
   * A Box-specific error code
   * @return code
  **/
  @Schema(example = "item_name_invalid", description = "A Box-specific error code")
  public CodeEnum getCode() {
    return code;
  }

  public void setCode(CodeEnum code) {
    this.code = code;
  }

  public ClientError message(String message) {
    this.message = message;
    return this;
  }

   /**
   * A short message describing the error.
   * @return message
  **/
  @Schema(example = "Method Not Allowed", description = "A short message describing the error.")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ClientError contextInfo(ClientErrorContextInfo contextInfo) {
    this.contextInfo = contextInfo;
    return this;
  }

   /**
   * Get contextInfo
   * @return contextInfo
  **/
  @Schema(description = "")
  public ClientErrorContextInfo getContextInfo() {
    return contextInfo;
  }

  public void setContextInfo(ClientErrorContextInfo contextInfo) {
    this.contextInfo = contextInfo;
  }

  public ClientError helpUrl(String helpUrl) {
    this.helpUrl = helpUrl;
    return this;
  }

   /**
   * A URL that links to more information about why this error occurred.
   * @return helpUrl
  **/
  @Schema(example = "http://developers.box.com/docs/#errors", description = "A URL that links to more information about why this error occurred.")
  public String getHelpUrl() {
    return helpUrl;
  }

  public void setHelpUrl(String helpUrl) {
    this.helpUrl = helpUrl;
  }

  public ClientError requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

   /**
   * A unique identifier for this response, which can be used when contacting Box support.
   * @return requestId
  **/
  @Schema(example = "abcdef123456", description = "A unique identifier for this response, which can be used when contacting Box support.")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientError clientError = (ClientError) o;
    return Objects.equals(this.type, clientError.type) &&
        Objects.equals(this.status, clientError.status) &&
        Objects.equals(this.code, clientError.code) &&
        Objects.equals(this.message, clientError.message) &&
        Objects.equals(this.contextInfo, clientError.contextInfo) &&
        Objects.equals(this.helpUrl, clientError.helpUrl) &&
        Objects.equals(this.requestId, clientError.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, status, code, message, contextInfo, helpUrl, requestId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientError {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    contextInfo: ").append(toIndentedString(contextInfo)).append("\n");
    sb.append("    helpUrl: ").append(toIndentedString(helpUrl)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
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
