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
 * Encryption states
 */
@ApiModel(description = "Encryption states")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class EncryptionInfo {
  /**
   * User key state
   */
  public enum UserKeyStateEnum {
    NONE("none"),
    
    PENDING("pending"),
    
    AVAILABLE("available");

    private String value;

    UserKeyStateEnum(String value) {
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
    public static UserKeyStateEnum fromValue(String text) {
      for (UserKeyStateEnum b : UserKeyStateEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("userKeyState")
  private UserKeyStateEnum userKeyState = null;

  /**
   * Room key state
   */
  public enum RoomKeyStateEnum {
    NONE("none"),
    
    PENDING("pending"),
    
    AVAILABLE("available");

    private String value;

    RoomKeyStateEnum(String value) {
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
    public static RoomKeyStateEnum fromValue(String text) {
      for (RoomKeyStateEnum b : RoomKeyStateEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("roomKeyState")
  private RoomKeyStateEnum roomKeyState = null;

  /**
   * DRACOON key state
   */
  public enum DataSpaceKeyStateEnum {
    NONE("none"),
    
    PENDING("pending"),
    
    AVAILABLE("available");

    private String value;

    DataSpaceKeyStateEnum(String value) {
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
    public static DataSpaceKeyStateEnum fromValue(String text) {
      for (DataSpaceKeyStateEnum b : DataSpaceKeyStateEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("dataSpaceKeyState")
  private DataSpaceKeyStateEnum dataSpaceKeyState = null;

  public EncryptionInfo userKeyState(UserKeyStateEnum userKeyState) {
    this.userKeyState = userKeyState;
    return this;
  }

   /**
   * User key state
   * @return userKeyState
  **/
  @ApiModelProperty(example = "none", required = true, value = "User key state")
  public UserKeyStateEnum getUserKeyState() {
    return userKeyState;
  }

  public void setUserKeyState(UserKeyStateEnum userKeyState) {
    this.userKeyState = userKeyState;
  }

  public EncryptionInfo roomKeyState(RoomKeyStateEnum roomKeyState) {
    this.roomKeyState = roomKeyState;
    return this;
  }

   /**
   * Room key state
   * @return roomKeyState
  **/
  @ApiModelProperty(example = "none", required = true, value = "Room key state")
  public RoomKeyStateEnum getRoomKeyState() {
    return roomKeyState;
  }

  public void setRoomKeyState(RoomKeyStateEnum roomKeyState) {
    this.roomKeyState = roomKeyState;
  }

  public EncryptionInfo dataSpaceKeyState(DataSpaceKeyStateEnum dataSpaceKeyState) {
    this.dataSpaceKeyState = dataSpaceKeyState;
    return this;
  }

   /**
   * DRACOON key state
   * @return dataSpaceKeyState
  **/
  @ApiModelProperty(example = "none", required = true, value = "DRACOON key state")
  public DataSpaceKeyStateEnum getDataSpaceKeyState() {
    return dataSpaceKeyState;
  }

  public void setDataSpaceKeyState(DataSpaceKeyStateEnum dataSpaceKeyState) {
    this.dataSpaceKeyState = dataSpaceKeyState;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EncryptionInfo encryptionInfo = (EncryptionInfo) o;
    return Objects.equals(this.userKeyState, encryptionInfo.userKeyState) &&
        Objects.equals(this.roomKeyState, encryptionInfo.roomKeyState) &&
        Objects.equals(this.dataSpaceKeyState, encryptionInfo.dataSpaceKeyState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userKeyState, roomKeyState, dataSpaceKeyState);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EncryptionInfo {\n");

      sb.append("    userKeyState: ").append(toIndentedString(userKeyState)).append("\n");
    sb.append("    roomKeyState: ").append(toIndentedString(roomKeyState)).append("\n");
    sb.append("    dataSpaceKeyState: ").append(toIndentedString(dataSpaceKeyState)).append("\n");
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

