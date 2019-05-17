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
 * User information
 */
@ApiModel(description = "User information")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class GroupUser {
    @JsonProperty("userInfo")
    private UserInfo userInfo = null;

  @JsonProperty("isMember")
  private Boolean isMember = null;

    @JsonProperty("id")
    private Long id = null;

  @JsonProperty("login")
  private String login = null;

  @JsonProperty("displayName")
  private String displayName = null;

  @JsonProperty("email")
  private String email = null;

    public GroupUser userInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    /**
     * Information about the user  [Since version 4.11.0]
     *
     * @return userInfo
     **/
    @ApiModelProperty(required = true, value = "Information about the user  [Since version 4.11.0]")
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public GroupUser isMember(Boolean isMember) {
        this.isMember = isMember;
        return this;
    }

    /**
     * Determines whether user is a member of the group or not
     *
     * @return isMember
     **/
    @ApiModelProperty(required = true, value = "Determines whether user is a member of the group or not")
    public Boolean isIsMember() {
        return isMember;
    }

    public void setIsMember(Boolean isMember) {
        this.isMember = isMember;
    }

  public GroupUser id(Long id) {
    this.id = id;
      return this;
  }

    /**
     * DEPRECATEDUnique identifier for the user use &#x60;id&#x60; from &#x60;UserInfo&#x60; instead  [Deprecated since version 4.11.0]
     * @return id
     **/
    @ApiModelProperty(required = true, value = "DEPRECATEDUnique identifier for the user use `id` from `UserInfo` instead  [Deprecated since version 4.11.0]")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GroupUser login(String login) {
    this.login = login;
      return this;
  }

    /**
     * DEPRECATEDUser login name will be removed  [Deprecated since version 4.11.0]
     * @return login
     **/
    @ApiModelProperty(required = true, value = "DEPRECATEDUser login name will be removed  [Deprecated since version 4.11.0]")
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public GroupUser displayName(String displayName) {
    this.displayName = displayName;
      return this;
  }

    /**
     * DEPRECATEDDisplay name use information from &#x60;UserInfo&#x60; instead to combine a display name  [Deprecated since version 4.11.0]
     * @return displayName
     **/
    @ApiModelProperty(required = true, value = "DEPRECATEDDisplay name use information from `UserInfo` instead to combine a display name  [Deprecated since version 4.11.0]")
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public GroupUser email(String email) {
    this.email = email;
      return this;
  }

    /**
     * DEPRECATEDEmail (not used) use &#x60;email&#x60; from &#x60;UserInfo&#x60; instead  [Deprecated since version 4.11.0]
     * @return email
     **/
    @ApiModelProperty(example = "john.doe@email.com", required = true, value = "DEPRECATEDEmail (not used) use `email` from `UserInfo` instead  [Deprecated since version 4.11.0]")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


    @Override
    public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupUser groupUser = (GroupUser) o;
        return Objects.equals(this.userInfo, groupUser.userInfo) &&
        Objects.equals(this.isMember, groupUser.isMember) &&
            Objects.equals(this.id, groupUser.id) &&
        Objects.equals(this.login, groupUser.login) &&
        Objects.equals(this.displayName, groupUser.displayName) &&
        Objects.equals(this.email, groupUser.email);
  }

  @Override
  public int hashCode() {
      return Objects.hash(userInfo, isMember, id, login, displayName, email);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupUser {\n");

      sb.append("    userInfo: ").append(toIndentedString(userInfo)).append("\n");
    sb.append("    isMember: ").append(toIndentedString(isMember)).append("\n");
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
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

