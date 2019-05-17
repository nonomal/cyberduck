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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Request model for creating an user
 */
@ApiModel(description = "Request model for creating an user")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class CreateUserRequest {
  @JsonProperty("firstName")
  private String firstName = null;

  @JsonProperty("lastName")
  private String lastName = null;

  @JsonProperty("authMethods")
  private List<UserAuthMethod> authMethods = new ArrayList<>();

  @JsonProperty("login")
  private String login = null;

  @JsonProperty("title")
  private String title = null;

  /**
   * Gender
   */
  public enum GenderEnum {
    M("m"),
    
    F("f"),
    
    N("n");

    private String value;

    GenderEnum(String value) {
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
    public static GenderEnum fromValue(String text) {
      for (GenderEnum b : GenderEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("gender")
  private GenderEnum gender = null;

  @JsonProperty("phone")
  private String phone = null;

  @JsonProperty("expiration")
  private ObjectExpiration expiration = null;

  @JsonProperty("receiverLanguage")
  private String receiverLanguage = null;

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("password")
    private String password = null;

    @JsonProperty("notifyUser")
    private Boolean notifyUser = null;

    @JsonProperty("needsToChangePassword")
    private Boolean needsToChangePassword = null;

  public CreateUserRequest firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

   /**
   * User first name
   * @return firstName
  **/
  @ApiModelProperty(required = true, value = "User first name")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public CreateUserRequest lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

   /**
   * User last name
   * @return lastName
  **/
  @ApiModelProperty(required = true, value = "User last name")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public CreateUserRequest authMethods(List<UserAuthMethod> authMethods) {
    this.authMethods = authMethods;
    return this;
  }

  public CreateUserRequest addAuthMethodsItem(UserAuthMethod authMethodsItem) {
    this.authMethods.add(authMethodsItem);
    return this;
  }

   /**
   * Authentication methods: * &#x60;sql&#x60; * &#x60;active_directory&#x60; * &#x60;radius&#x60; * &#x60;openid&#x60;
   * @return authMethods
  **/
  @ApiModelProperty(required = true, value = "Authentication methods: * `sql` * `active_directory` * `radius` * `openid`")
  public List<UserAuthMethod> getAuthMethods() {
    return authMethods;
  }

  public void setAuthMethods(List<UserAuthMethod> authMethods) {
    this.authMethods = authMethods;
  }

  public CreateUserRequest login(String login) {
    this.login = login;
    return this;
  }

   /**
   * User login name
   * @return login
  **/
  @ApiModelProperty(required = true, value = "User login name")
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public CreateUserRequest title(String title) {
    this.title = title;
    return this;
  }

   /**
   * Job title
   * @return title
  **/
  @ApiModelProperty(value = "Job title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CreateUserRequest gender(GenderEnum gender) {
    this.gender = gender;
    return this;
  }

   /**
   * Gender
   * @return gender
  **/
  @ApiModelProperty(example = "n", value = "Gender")
  public GenderEnum getGender() {
    return gender;
  }

  public void setGender(GenderEnum gender) {
    this.gender = gender;
  }

  public CreateUserRequest phone(String phone) {
    this.phone = phone;
    return this;
  }

   /**
    * Phone number
   * @return phone
  **/
   @ApiModelProperty(value = "Phone number")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public CreateUserRequest expiration(ObjectExpiration expiration) {
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

  public CreateUserRequest receiverLanguage(String receiverLanguage) {
    this.receiverLanguage = receiverLanguage;
    return this;
  }

   /**
   * IETF language tag
   * @return receiverLanguage
  **/
  @ApiModelProperty(example = "de-DE", value = "IETF language tag")
  public String getReceiverLanguage() {
    return receiverLanguage;
  }

  public void setReceiverLanguage(String receiverLanguage) {
    this.receiverLanguage = receiverLanguage;
  }

    public CreateUserRequest email(String email) {
        this.email = email;
        return this;
    }

    /**
     * Email (not used)
     *
     * @return email
     **/
    @ApiModelProperty(example = "john.doe@email.com", value = "Email (not used)")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CreateUserRequest password(String password) {
        this.password = password;
        return this;
    }

    /**
     * An initial password may be preset
     *
     * @return password
     **/
    @ApiModelProperty(value = "An initial password may be preset")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CreateUserRequest notifyUser(Boolean notifyUser) {
        this.notifyUser = notifyUser;
        return this;
    }

    /**
     * Notify user about his new account (default: &#x60;true&#x60;)
     *
     * @return notifyUser
     **/
    @ApiModelProperty(value = "Notify user about his new account (default: `true`)")
    public Boolean isNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(Boolean notifyUser) {
        this.notifyUser = notifyUser;
    }

    public CreateUserRequest needsToChangePassword(Boolean needsToChangePassword) {
        this.needsToChangePassword = needsToChangePassword;
        return this;
    }

    /**
     * Determines whether user has to change his / her initial password. (default: &#x60;true&#x60;)
     *
     * @return needsToChangePassword
     **/
    @ApiModelProperty(value = "Determines whether user has to change his / her initial password. (default: `true`)")
    public Boolean isNeedsToChangePassword() {
        return needsToChangePassword;
    }

    public void setNeedsToChangePassword(Boolean needsToChangePassword) {
        this.needsToChangePassword = needsToChangePassword;
    }


    @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateUserRequest createUserRequest = (CreateUserRequest) o;
    return Objects.equals(this.firstName, createUserRequest.firstName) &&
        Objects.equals(this.lastName, createUserRequest.lastName) &&
        Objects.equals(this.authMethods, createUserRequest.authMethods) &&
        Objects.equals(this.login, createUserRequest.login) &&
        Objects.equals(this.title, createUserRequest.title) &&
        Objects.equals(this.gender, createUserRequest.gender) &&
        Objects.equals(this.phone, createUserRequest.phone) &&
        Objects.equals(this.expiration, createUserRequest.expiration) &&
        Objects.equals(this.receiverLanguage, createUserRequest.receiverLanguage) &&
        Objects.equals(this.email, createUserRequest.email) &&
        Objects.equals(this.password, createUserRequest.password) &&
        Objects.equals(this.notifyUser, createUserRequest.notifyUser) &&
        Objects.equals(this.needsToChangePassword, createUserRequest.needsToChangePassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, authMethods, login, title, gender, phone, expiration, receiverLanguage, email, password, notifyUser, needsToChangePassword);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateUserRequest {\n");
    
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    authMethods: ").append(toIndentedString(authMethods)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
    sb.append("    receiverLanguage: ").append(toIndentedString(receiverLanguage)).append("\n");
      sb.append("    email: ").append(toIndentedString(email)).append("\n");
      sb.append("    password: ").append(toIndentedString(password)).append("\n");
      sb.append("    notifyUser: ").append(toIndentedString(notifyUser)).append("\n");
      sb.append("    needsToChangePassword: ").append(toIndentedString(needsToChangePassword)).append("\n");
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

