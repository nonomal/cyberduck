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

import org.joda.time.DateTime;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DEPRECATED Syslog event information
 */
@ApiModel(description = "DEPRECATED Syslog event information")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class SyslogEvent {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("time")
  private DateTime time = null;

  @JsonProperty("userId")
  private Long userId = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("operationId")
  private Integer operationId = null;

  @JsonProperty("operationName")
  private String operationName = null;

  /**
   * Operation status: * &#x60;0&#x60; - Success * &#x60;2&#x60; - Error
   */
  public enum StatusEnum {
    NUMBER_0(0),
    
    NUMBER_2(2);

    private Integer value;

    StatusEnum(Integer value) {
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
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("status")
  private StatusEnum status = null;

  @JsonProperty("userClient")
  private String userClient = null;

  @JsonProperty("customerId")
  private Long customerId = null;

  @JsonProperty("userName")
  private String userName = null;

  @JsonProperty("userIp")
  private String userIp = null;

  @JsonProperty("authParentSource")
  private String authParentSource = null;

  @JsonProperty("authParentTarget")
  private String authParentTarget = null;

  @JsonProperty("objectId1")
  private Long objectId1 = null;

  @JsonProperty("objectType1")
  private Integer objectType1 = null;

  @JsonProperty("objectName1")
  private String objectName1 = null;

  @JsonProperty("objectId2")
  private Long objectId2 = null;

  @JsonProperty("objectType2")
  private Integer objectType2 = null;

  @JsonProperty("objectName2")
  private String objectName2 = null;

  @JsonProperty("attribute1")
  private String attribute1 = null;

  @JsonProperty("attribute2")
  private String attribute2 = null;

  @JsonProperty("attribute3")
  private String attribute3 = null;

  public SyslogEvent id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Event ID
   * @return id
  **/
  @ApiModelProperty(required = true, value = "Event ID")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SyslogEvent time(DateTime time) {
    this.time = time;
    return this;
  }

   /**
   * Event timestamp
   * @return time
  **/
  @ApiModelProperty(example = "2018-01-01T00:00:00", required = true, value = "Event timestamp")
  public DateTime getTime() {
    return time;
  }

  public void setTime(DateTime time) {
    this.time = time;
  }

  public SyslogEvent userId(Long userId) {
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

  public SyslogEvent message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Event description
   * @return message
  **/
  @ApiModelProperty(required = true, value = "Event description")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public SyslogEvent operationId(Integer operationId) {
    this.operationId = operationId;
    return this;
  }

   /**
   * Operation type ID
   * @return operationId
  **/
  @ApiModelProperty(value = "Operation type ID")
  public Integer getOperationId() {
    return operationId;
  }

  public void setOperationId(Integer operationId) {
    this.operationId = operationId;
  }

  public SyslogEvent operationName(String operationName) {
    this.operationName = operationName;
    return this;
  }

   /**
   * Operation name
   * @return operationName
  **/
  @ApiModelProperty(value = "Operation name")
  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public SyslogEvent status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Operation status: * &#x60;0&#x60; - Success * &#x60;2&#x60; - Error
   * @return status
  **/
  @ApiModelProperty(value = "Operation status: * `0` - Success * `2` - Error")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public SyslogEvent userClient(String userClient) {
    this.userClient = userClient;
    return this;
  }

   /**
   * Client
   * @return userClient
  **/
  @ApiModelProperty(value = "Client")
  public String getUserClient() {
    return userClient;
  }

  public void setUserClient(String userClient) {
    this.userClient = userClient;
  }

  public SyslogEvent customerId(Long customerId) {
    this.customerId = customerId;
    return this;
  }

   /**
   * Unique identifier for the customer
   * @return customerId
  **/
  @ApiModelProperty(value = "Unique identifier for the customer")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public SyslogEvent userName(String userName) {
    this.userName = userName;
    return this;
  }

   /**
   * User name
   * @return userName
  **/
  @ApiModelProperty(value = "User name")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public SyslogEvent userIp(String userIp) {
    this.userIp = userIp;
    return this;
  }

   /**
   * User IP
   * @return userIp
  **/
  @ApiModelProperty(value = "User IP")
  public String getUserIp() {
    return userIp;
  }

  public void setUserIp(String userIp) {
    this.userIp = userIp;
  }

  public SyslogEvent authParentSource(String authParentSource) {
    this.authParentSource = authParentSource;
    return this;
  }

   /**
   * Auth parent source ID
   * @return authParentSource
  **/
  @ApiModelProperty(value = "Auth parent source ID")
  public String getAuthParentSource() {
    return authParentSource;
  }

  public void setAuthParentSource(String authParentSource) {
    this.authParentSource = authParentSource;
  }

  public SyslogEvent authParentTarget(String authParentTarget) {
    this.authParentTarget = authParentTarget;
    return this;
  }

   /**
   * Auth parent target ID
   * @return authParentTarget
  **/
  @ApiModelProperty(value = "Auth parent target ID")
  public String getAuthParentTarget() {
    return authParentTarget;
  }

  public void setAuthParentTarget(String authParentTarget) {
    this.authParentTarget = authParentTarget;
  }

  public SyslogEvent objectId1(Long objectId1) {
    this.objectId1 = objectId1;
    return this;
  }

   /**
   * Object ID 1
   * @return objectId1
  **/
  @ApiModelProperty(value = "Object ID 1")
  public Long getObjectId1() {
    return objectId1;
  }

  public void setObjectId1(Long objectId1) {
    this.objectId1 = objectId1;
  }

  public SyslogEvent objectType1(Integer objectType1) {
    this.objectType1 = objectType1;
    return this;
  }

   /**
   * Object type 1
   * @return objectType1
  **/
  @ApiModelProperty(value = "Object type 1")
  public Integer getObjectType1() {
    return objectType1;
  }

  public void setObjectType1(Integer objectType1) {
    this.objectType1 = objectType1;
  }

  public SyslogEvent objectName1(String objectName1) {
    this.objectName1 = objectName1;
    return this;
  }

   /**
   * Object name 1
   * @return objectName1
  **/
  @ApiModelProperty(value = "Object name 1")
  public String getObjectName1() {
    return objectName1;
  }

  public void setObjectName1(String objectName1) {
    this.objectName1 = objectName1;
  }

  public SyslogEvent objectId2(Long objectId2) {
    this.objectId2 = objectId2;
    return this;
  }

   /**
   * Object ID 2
   * @return objectId2
  **/
  @ApiModelProperty(value = "Object ID 2")
  public Long getObjectId2() {
    return objectId2;
  }

  public void setObjectId2(Long objectId2) {
    this.objectId2 = objectId2;
  }

  public SyslogEvent objectType2(Integer objectType2) {
    this.objectType2 = objectType2;
    return this;
  }

   /**
   * Object type 2
   * @return objectType2
  **/
  @ApiModelProperty(value = "Object type 2")
  public Integer getObjectType2() {
    return objectType2;
  }

  public void setObjectType2(Integer objectType2) {
    this.objectType2 = objectType2;
  }

  public SyslogEvent objectName2(String objectName2) {
    this.objectName2 = objectName2;
    return this;
  }

   /**
   * Object type 2
   * @return objectName2
  **/
  @ApiModelProperty(value = "Object type 2")
  public String getObjectName2() {
    return objectName2;
  }

  public void setObjectName2(String objectName2) {
    this.objectName2 = objectName2;
  }

  public SyslogEvent attribute1(String attribute1) {
    this.attribute1 = attribute1;
    return this;
  }

   /**
   * Attribute 1
   * @return attribute1
  **/
  @ApiModelProperty(value = "Attribute 1")
  public String getAttribute1() {
    return attribute1;
  }

  public void setAttribute1(String attribute1) {
    this.attribute1 = attribute1;
  }

  public SyslogEvent attribute2(String attribute2) {
    this.attribute2 = attribute2;
    return this;
  }

   /**
   * Attribute 2
   * @return attribute2
  **/
  @ApiModelProperty(value = "Attribute 2")
  public String getAttribute2() {
    return attribute2;
  }

  public void setAttribute2(String attribute2) {
    this.attribute2 = attribute2;
  }

  public SyslogEvent attribute3(String attribute3) {
    this.attribute3 = attribute3;
    return this;
  }

   /**
   * Attribute 3
   * @return attribute3
  **/
  @ApiModelProperty(value = "Attribute 3")
  public String getAttribute3() {
    return attribute3;
  }

  public void setAttribute3(String attribute3) {
    this.attribute3 = attribute3;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SyslogEvent syslogEvent = (SyslogEvent) o;
    return Objects.equals(this.id, syslogEvent.id) &&
        Objects.equals(this.time, syslogEvent.time) &&
        Objects.equals(this.userId, syslogEvent.userId) &&
        Objects.equals(this.message, syslogEvent.message) &&
        Objects.equals(this.operationId, syslogEvent.operationId) &&
        Objects.equals(this.operationName, syslogEvent.operationName) &&
        Objects.equals(this.status, syslogEvent.status) &&
        Objects.equals(this.userClient, syslogEvent.userClient) &&
        Objects.equals(this.customerId, syslogEvent.customerId) &&
        Objects.equals(this.userName, syslogEvent.userName) &&
        Objects.equals(this.userIp, syslogEvent.userIp) &&
        Objects.equals(this.authParentSource, syslogEvent.authParentSource) &&
        Objects.equals(this.authParentTarget, syslogEvent.authParentTarget) &&
        Objects.equals(this.objectId1, syslogEvent.objectId1) &&
        Objects.equals(this.objectType1, syslogEvent.objectType1) &&
        Objects.equals(this.objectName1, syslogEvent.objectName1) &&
        Objects.equals(this.objectId2, syslogEvent.objectId2) &&
        Objects.equals(this.objectType2, syslogEvent.objectType2) &&
        Objects.equals(this.objectName2, syslogEvent.objectName2) &&
        Objects.equals(this.attribute1, syslogEvent.attribute1) &&
        Objects.equals(this.attribute2, syslogEvent.attribute2) &&
        Objects.equals(this.attribute3, syslogEvent.attribute3);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, time, userId, message, operationId, operationName, status, userClient, customerId, userName, userIp, authParentSource, authParentTarget, objectId1, objectType1, objectName1, objectId2, objectType2, objectName2, attribute1, attribute2, attribute3);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SyslogEvent {\n");

      sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    operationId: ").append(toIndentedString(operationId)).append("\n");
    sb.append("    operationName: ").append(toIndentedString(operationName)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    userClient: ").append(toIndentedString(userClient)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    userIp: ").append(toIndentedString(userIp)).append("\n");
    sb.append("    authParentSource: ").append(toIndentedString(authParentSource)).append("\n");
    sb.append("    authParentTarget: ").append(toIndentedString(authParentTarget)).append("\n");
    sb.append("    objectId1: ").append(toIndentedString(objectId1)).append("\n");
    sb.append("    objectType1: ").append(toIndentedString(objectType1)).append("\n");
    sb.append("    objectName1: ").append(toIndentedString(objectName1)).append("\n");
    sb.append("    objectId2: ").append(toIndentedString(objectId2)).append("\n");
    sb.append("    objectType2: ").append(toIndentedString(objectType2)).append("\n");
    sb.append("    objectName2: ").append(toIndentedString(objectName2)).append("\n");
    sb.append("    attribute1: ").append(toIndentedString(attribute1)).append("\n");
    sb.append("    attribute2: ").append(toIndentedString(attribute2)).append("\n");
    sb.append("    attribute3: ").append(toIndentedString(attribute3)).append("\n");
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

